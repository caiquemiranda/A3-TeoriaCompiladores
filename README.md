# A3-TeoriaCompiladores

Repositório com os arquivos e scripts utilizados para desenvolver a proposta de projeto de A3 da UC de TEORIA DA COMPUTAÇÃO E COMPILADORES - Semestre 02/2025.


---

## Testes do Compilador Marte

O diretório `TestCodes/` contém os arquivos de teste para o compilador da linguagem Marte.

### Estrutura

```
TestCodes/
├── Success/              # Programas que devem compilar e executar
│   ├── calculadora.mar
│   ├── operacoes_basicas.mar
│   ├── estruturas_controle.mar
│   └── precedencia.mar
├── Errors/                # Programas com erros intencionais
│   ├── erro_variavel_nao_declarada.mar
│   ├── erro_sintaxe_faltando_ponto_virgula.mar
│   ├── erro_tipo_incompativel.mar
│   ├── erro_palavra_reservada.mar
│   ├── erro_expressao_invalida.mar
│   ├── erro_parenteses_nao_fechados.mar
│   ├── erro_leia_variavel_nao_declarada.mar
│   └── erro_for_sintaxe_invalida.mar
├── executar_teste_sucesso.bat
├── executar_teste_erros.bat
├── gerar_relatorio_teste.bat
├── relatorio_testes.html
└── relatorio_testes.txt
```

### Composição

### Testes de Sucesso

- **calculadora.mar**: Calculadora simples que lê dois números e realiza as quatro operações básicas.
- **operacoes_basicas.mar**: Demonstra os três tipos de variáveis (int, float, bool) e operações básicas.
- **estruturas_controle.mar**: Testa todas as estruturas de controle: if-else, while e for.
- **precedencia.mar**: Testa a precedência de operadores matemáticos.

### Testes de Erros

Cada arquivo na pasta `Errors/` contém um erro específico:

- **erro_variavel_nao_declarada.mar**: Variável usada sem declaração
- **erro_sintaxe_faltando_ponto_virgula.mar**: Falta ponto e vírgula
- **erro_tipo_incompativel.mar**: Atribuição de tipo incompatível
- **erro_palavra_reservada.mar**: Uso de palavra reservada como variável
- **erro_expressao_invalida.mar**: Expressão incompleta
- **erro_parenteses_nao_fechados.mar**: Parênteses não balanceados
- **erro_leia_variavel_nao_declarada.mar**: Leitura de variável não declarada
- **erro_for_sintaxe_invalida.mar**: Sintaxe incorreta no for

### Pré-requisitos

1. O projeto deve ser compilado na pasta `compilador` abrindo o _powersheel_ e rodando o comando:
   ```bash
   mvn package
   ```

2. Validar o *JAR* no caminho: `compilador/target/compilador-1.0-SNAPSHOT.jar`

3. *Java* deve estar instalado e no *PATH* do sistema

### Execução de testes

#### Executar Todos os Testes de Sucesso

- Acesse a pasta _TestCodes_

- Execute o script no _powersheel_:
```
.\executar_teste_sucesso.bat
```

O que esse script faz:
- Executa cada arquivo `.mar` da pasta `Success/`
- Compila e executa cada programa
- Mostra os resultados no terminal

#### Executar Todos os Testes de Erros

Execute o script:
```bash
cd TestCodes
executar_teste_erros.bat
```


#### Executar Todos os Testes completos

- Acesse a pasta _TestCodes_

- Execute o script no _powersheel_:
```
.\gerar_relatorio_teste.bat
```

O que esse script faz:
- Executa cada arquivo `.mar` das pastas `Success/` e `Errors/`
- Compila e executa cada programa
- E cria o arquivo `relatorio_testes.txt` com todos os resultados


### Notas

- Os scripts assumem que você está executando a partir do diretório `TestCodes/`
- O compilador espera encontrar o arquivo `teste.mar` no diretório `compilador/`
- O documento com os testes executados e registrados na pasta `Doc` com o nome `Documentação dos Erros de Compilação.docx`