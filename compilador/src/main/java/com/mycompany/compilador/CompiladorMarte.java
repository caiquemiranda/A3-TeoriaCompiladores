package com.mycompany.compilador;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CompiladorMarte {
    // Flag para controlar erros
    private static boolean temErro = false;
    
    // ErrorListener inline
    private static class ErrorListener extends BaseErrorListener {
        @Override
        public void syntaxError(Recognizer<?, ?> recognizer,
                                Object offendingSymbol,
                                int line,
                                int charPositionInLine,
                                String msg,
                                RecognitionException e) {
            temErro = true;
            System.err.println("Erro sintático na linha " + line + ", coluna " + charPositionInLine + ": " + msg);
        }
    }
    
    // Visitor inline para gerar código Java
    private static class CodeGenerator extends MarteBaseVisitor<String> {
        private StringBuilder codigo = new StringBuilder();
        private Map<String, String> variaveis = new HashMap<>();
        private int indent = 0;
        
        private String indentacao() {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < indent; i++) sb.append(" ");
            return sb.toString();
        }
        
        @Override
        public String visitProgram(MarteParser.ProgramContext ctx) {
            codigo.append("import java.util.Scanner;\n\n");
            codigo.append("public class Resultado {\n");
            codigo.append("    private static Scanner scanner = new Scanner(System.in);\n\n");
            codigo.append("    public static void main(String[] args) {\n");
            indent = 2;
            for (int i = 0; i < ctx.getChildCount() - 1; i++) {
                visit(ctx.getChild(i));
            }
            indent = 0;
            codigo.append("    }\n}\n");
            return codigo.toString();
        }
        
        @Override
        public String visitDeclaration(MarteParser.DeclarationContext ctx) {
            String tipo = visitType(ctx.type());
            String nome = ctx.ID().getText();
            variaveis.put(nome, tipo);
            codigo.append(indentacao()).append(tipo).append(" ").append(nome);
            if (ctx.expr() != null) {
                codigo.append(" = ").append(visit(ctx.expr()));
            }
            codigo.append(";\n");
            return null;
        }
        
        @Override
        public String visitType(MarteParser.TypeContext ctx) {
            if (ctx.INTEIRO() != null) return "int";
            if (ctx.DECIMAL() != null) return "float";
            if (ctx.BOOLEANO() != null) return "boolean";
            return "Object";
        }
        
        @Override
        public String visitStatement(MarteParser.StatementContext ctx) {
            if (ctx.block() != null) visitBlock(ctx.block());
            else if (ctx.ifStmtNoElse() != null) visitIfStmtNoElse(ctx.ifStmtNoElse());
            else if (ctx.ifStmtWithElse() != null) visitIfStmtWithElse(ctx.ifStmtWithElse());
            else if (ctx.whileStmt() != null) visitWhileStmt(ctx.whileStmt());
            else if (ctx.forStmt() != null) visitForStmt(ctx.forStmt());
            else if (ctx.assignStmt() != null) visitAssignStmt(ctx.assignStmt());
            else if (ctx.scanfStmt() != null) visitScanfStmt(ctx.scanfStmt());
            else if (ctx.printfStmt() != null) visitPrintfStmt(ctx.printfStmt());
            return null;
        }
        
        @Override
        public String visitBlock(MarteParser.BlockContext ctx) {
            codigo.append(indentacao()).append("{\n");
            indent += 2;
            for (int i = 1; i < ctx.getChildCount() - 1; i++) {
                visit(ctx.getChild(i));
            }
            indent -= 2;
            codigo.append(indentacao()).append("}\n");
            return null;
        }
        
        @Override
        public String visitIfStmtNoElse(MarteParser.IfStmtNoElseContext ctx) {
            codigo.append(indentacao()).append("if (").append(visit(ctx.expr())).append(") ");
            if (ctx.statement().block() == null) {
                codigo.append("\n");
                indent += 2;
                visit(ctx.statement());
                indent -= 2;
            } else {
                visit(ctx.statement());
            }
            return null;
        }
        
        @Override
        public String visitIfStmtWithElse(MarteParser.IfStmtWithElseContext ctx) {
            codigo.append(indentacao()).append("if (").append(visit(ctx.expr())).append(") ");
            if (ctx.statement(0).block() == null) {
                codigo.append("\n");
                indent += 2;
                visit(ctx.statement(0));
                indent -= 2;
            } else {
                visit(ctx.statement(0));
            }
            codigo.append(indentacao()).append("else ");
            if (ctx.statement(1).block() == null) {
                codigo.append("\n");
                indent += 2;
                visit(ctx.statement(1));
                indent -= 2;
            } else {
                visit(ctx.statement(1));
            }
            return null;
        }
        
        @Override
        public String visitWhileStmt(MarteParser.WhileStmtContext ctx) {
            codigo.append(indentacao()).append("while (").append(visit(ctx.expr())).append(") ");
            if (ctx.statement().block() == null) {
                codigo.append("\n");
                indent += 2;
                visit(ctx.statement());
                indent -= 2;
            } else {
                visit(ctx.statement());
            }
            return null;
        }
        
        // Método auxiliar para gerar apenas a expressão de atribuição (sem ponto e vírgula)
        private String gerarAtribuicao(MarteParser.AssignStmtContext ctx) {
            return ctx.ID().getText() + " = " + visit(ctx.expr());
        }
        
        // Método auxiliar para gerar apenas a declaração (sem ponto e vírgula)
        private String gerarDeclaracao(MarteParser.DeclarationContext ctx) {
            String tipo = visitType(ctx.type());
            String nome = ctx.ID().getText();
            String result = tipo + " " + nome;
            if (ctx.expr() != null) {
                result += " = " + visit(ctx.expr());
            }
            return result;
        }
        
        @Override
        public String visitForStmt(MarteParser.ForStmtContext ctx) {
            codigo.append(indentacao()).append("for (");
            if (ctx.forInit().declaration() != null) {
                codigo.append(gerarDeclaracao(ctx.forInit().declaration()));
            } else if (ctx.forInit().ID() != null) {
                codigo.append(ctx.forInit().ID().getText())
                      .append(" = ").append(visit(ctx.forInit().expr()));
            }
            codigo.append("; ").append(visit(ctx.expr())).append("; ");
            if (ctx.forStep().ID() != null) {
                codigo.append(ctx.forStep().ID().getText())
                      .append(" = ").append(visit(ctx.forStep().expr()));
            }
            codigo.append(") ");
            if (ctx.statement().block() == null) {
                codigo.append("\n");
                indent += 2;
                visit(ctx.statement());
                indent -= 2;
            } else {
                visit(ctx.statement());
            }
            return null;
        }
        
        @Override
        public String visitAssignStmt(MarteParser.AssignStmtContext ctx) {
            codigo.append(indentacao()).append(ctx.ID().getText())
                   .append(" = ").append(visit(ctx.expr())).append(";\n");
            return null;
        }
        
        @Override
        public String visitScanfStmt(MarteParser.ScanfStmtContext ctx) {
            String nome = ctx.ID().getText();
            String tipo = variaveis.get(nome);
            if ("int".equals(tipo)) {
                codigo.append(indentacao()).append(nome).append(" = scanner.nextInt();\n");
            } else if ("float".equals(tipo)) {
                codigo.append(indentacao()).append(nome).append(" = scanner.nextFloat();\n");
            } else if ("boolean".equals(tipo)) {
                codigo.append(indentacao()).append(nome).append(" = scanner.nextBoolean();\n");
            } else {
                codigo.append(indentacao()).append(nome).append(" = scanner.next();\n");
            }
            return null;
        }
        
        @Override
        public String visitPrintfStmt(MarteParser.PrintfStmtContext ctx) {
            codigo.append(indentacao()).append("System.out.println(")
                   .append(visit(ctx.expr())).append(");\n");
            return null;
        }
        
        @Override
        public String visitExpr(MarteParser.ExprContext ctx) {
            return visit(ctx.orExpr());
        }
        
        @Override
        public String visitOrExpr(MarteParser.OrExprContext ctx) {
            if (ctx.getChildCount() == 1) return visit(ctx.andExpr(0));
            StringBuilder sb = new StringBuilder("(");
            for (int i = 0; i < ctx.andExpr().size(); i++) {
                if (i > 0) sb.append(" || ");
                sb.append(visit(ctx.andExpr(i)));
            }
            return sb.append(")").toString();
        }
        
        @Override
        public String visitAndExpr(MarteParser.AndExprContext ctx) {
            if (ctx.getChildCount() == 1) return visit(ctx.eqExpr(0));
            StringBuilder sb = new StringBuilder("(");
            for (int i = 0; i < ctx.eqExpr().size(); i++) {
                if (i > 0) sb.append(" && ");
                sb.append(visit(ctx.eqExpr(i)));
            }
            return sb.append(")").toString();
        }
        
        @Override
        public String visitEqExpr(MarteParser.EqExprContext ctx) {
            if (ctx.getChildCount() == 1) return visit(ctx.relExpr(0));
            StringBuilder sb = new StringBuilder("(");
            for (int i = 0; i < ctx.relExpr().size(); i++) {
                if (i > 0) {
                    // Os operadores estão nas posições ímpares: 1, 3, 5, etc
                    String op = ctx.getChild(i * 2 - 1).getText();
                    sb.append(" ").append(op).append(" ");
                }
                sb.append(visit(ctx.relExpr(i)));
            }
            return sb.append(")").toString();
        }
        
        @Override
        public String visitRelExpr(MarteParser.RelExprContext ctx) {
            if (ctx.getChildCount() == 1) return visit(ctx.addExpr(0));
            StringBuilder sb = new StringBuilder("(");
            for (int i = 0; i < ctx.addExpr().size(); i++) {
                if (i > 0) {
                    // Os operadores estão nas posições ímpares: 1, 3, 5, etc
                    String op = ctx.getChild(i * 2 - 1).getText();
                    sb.append(" ").append(op).append(" ");
                }
                sb.append(visit(ctx.addExpr(i)));
            }
            return sb.append(")").toString();
        }
        
        @Override
        public String visitAddExpr(MarteParser.AddExprContext ctx) {
            if (ctx.getChildCount() == 1) return visit(ctx.mulExpr(0));
            StringBuilder sb = new StringBuilder("(");
            for (int i = 0; i < ctx.mulExpr().size(); i++) {
                if (i > 0) {
                    // Os operadores estão nas posições ímpares: 1, 3, 5, etc
                    String op = ctx.getChild(i * 2 - 1).getText();
                    sb.append(" ").append(op).append(" ");
                }
                sb.append(visit(ctx.mulExpr(i)));
            }
            return sb.append(")").toString();
        }
        
        @Override
        public String visitMulExpr(MarteParser.MulExprContext ctx) {
            if (ctx.getChildCount() == 1) return visit(ctx.unary(0));
            StringBuilder sb = new StringBuilder("(");
            for (int i = 0; i < ctx.unary().size(); i++) {
                if (i > 0) {
                    // Os operadores estão nas posições ímpares: 1, 3, 5, etc
                    String op = ctx.getChild(i * 2 - 1).getText();
                    sb.append(" ").append(op).append(" ");
                }
                sb.append(visit(ctx.unary(i)));
            }
            return sb.append(")").toString();
        }
        
        @Override
        public String visitUnary(MarteParser.UnaryContext ctx) {
            if (ctx.getChildCount() == 1) return visit(ctx.primary());
            return ctx.getChild(0).getText() + visit(ctx.unary());
        }
        
        @Override
        public String visitPrimary(MarteParser.PrimaryContext ctx) {
            if (ctx.expr() != null) return "(" + visit(ctx.expr()) + ")";
            if (ctx.literal() != null) return visit(ctx.literal());
            if (ctx.ID() != null) return ctx.ID().getText();
            return "";
        }
        
        @Override
        public String visitLiteral(MarteParser.LiteralContext ctx) {
            if (ctx.BOOL_LIT() != null) return ctx.BOOL_LIT().getText();
            if (ctx.NUMBER() != null) {
                String num = ctx.NUMBER().getText();
                // Se o número contém ponto decimal, adiciona 'f' para float
                if (num.contains(".") && !num.endsWith("f") && !num.endsWith("F")) {
                    return num + "f";
                }
                return num;
            }
            return "";
        }
    }
    
    public static void main(String[] args) {
        try {
            String arquivoFonte = "teste.mar";
            CharStream input = CharStreams.fromFileName(arquivoFonte);

            // 1. Lexer
            MarteLexer lexer = new MarteLexer(input);
            lexer.removeErrorListeners();
            lexer.addErrorListener(new ErrorListener());
            CommonTokenStream tokens = new CommonTokenStream(lexer);

            // 2. Parser
            MarteParser parser = new MarteParser(tokens);
            parser.removeErrorListeners();
            parser.addErrorListener(new ErrorListener());
            
            // Reseta flag de erro
            temErro = false;

            // 3. Parse
            ParseTree tree = parser.program();

            if (temErro) {
                System.err.println("Compilação cancelada devido a erros sintáticos.");
                return;
            }

            // 4. Visitor (Geração de Código)
            CodeGenerator visitor = new CodeGenerator();
            String codigoGerado = visitor.visit(tree);

            // 5. Salvar arquivo Java
            try (PrintWriter out = new PrintWriter("Resultado.java")) {
                out.println(codigoGerado);
            }
            
            System.out.println("--- Código Java gerado com sucesso. ---");

            // ============================================================
            // 6. AUTOMATIZAÇÃO: Compilar e Executar o código gerado
            // ============================================================
            
            // Passo A: Chamar o 'javac Resultado.java'
            System.out.println("Compilando o arquivo Resultado.java...");
            ProcessBuilder compileProcess = new ProcessBuilder("javac", "Resultado.java");
            
            // inheritIO faz com que a saída do comando apareça no console do NetBeans
            compileProcess.inheritIO(); 
            Process pCompile = compileProcess.start();
            int compileResult = pCompile.waitFor(); // Espera terminar

            if (compileResult != 0) {
                System.err.println("Erro: O código Java gerado contém erros e não pôde ser compilado.");
                return;
            }

            // Passo B: Chamar o 'java Resultado'
            System.out.println("Executando o programa Marte...");
            System.out.println("----------------------------------");
            
            ProcessBuilder runProcess = new ProcessBuilder("java", "Resultado");
            runProcess.inheritIO(); // Permite que você interaja (digite input) no console
            Process pRun = runProcess.start();
            pRun.waitFor(); // Espera o programa terminar

            System.out.println("\n----------------------------------");
            System.out.println("Execução finalizada.");

        } catch (RuntimeException e) {
            System.err.println("Erro Semântico: " + e.getMessage());
        } catch (IOException | InterruptedException e) {
            System.err.println("Erro ao tentar executar o compilador Java: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
