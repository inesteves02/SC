# Projeto Tintol Market

Este projeto é uma aplicação distribuída do tipo cliente-servidor que oferece um serviço de compra e venda de vinhos aos usuários. A aplicação é dividida em duas fases, sendo que nesta primeira fase, o objetivo é criar as funcionalidades da aplicação sem se preocupar com a segurança da informação.

## Arquitetura do Sistema

O projeto consiste na construção de dois programas: o servidor TintolmarketServer e a aplicação cliente Tintolmarket que se conecta ao servidor via sockets TCP. O servidor executa em uma máquina e pode receber um número ilimitado de clientes em máquinas diferentes na Internet.

## Funcionalidades

A aplicação deve ser executada da seguinte maneira:

1. Servidor:
   TintolmarketServer <port>
   - <port> identifica o porto (TCP) para aceitar ligações de clientes. Por omissão o servidor deve usar o porto 12345.

2. Cliente:
   Tintolmarket <serverAddress> <userID> [password]
   Em que:
   - <serverAddress> identifica o servidor. O formato de serverAddress é o seguinte: <IP/hostname>[:Port]. O endereço IP ou o hostname do servidor são obrigatórios e o porto é opcional. Por omissão, o cliente deve ligar-se ao porto 12345 do servidor.
   - <clientID> identifica o utilizador local.
   - [password] – password utilizada para autenticar o utilizador local. Caso a password não seja dada na linha de comando, deve ser pedida ao utilizador pela aplicação.

A aplicação cliente deve enviar o clientID e a password ao servidor para autenticar o usuário. Caso o usuário não esteja registrado no servidor, ele fará o registro do novo usuário, adicionando o clientID e a respectiva password ao arquivo de usuários do servidor. As estruturas de dados que mantêm informações relativas a cada usuário também devem ser inicializadas. Cada usuário deve ter um saldo mantido pelo servidor cujo valor inicial será 200.

Se o usuário não conseguir se autenticar no servidor, a aplicação deve terminar, assinalando o erro correspondente. Caso contrário, a aplicação deve apresentar um menu com os seguintes comandos:

- add <wine> <image>: adiciona um novo vinho identificado por wine, associado à imagem image. Caso já exista um vinho com o mesmo nome deve ser devolvido um erro. Inicialmente, o vinho não terá qualquer classificação e o número de unidades disponíveis será zero.
- sell <wine> <value> <quantity>: coloca à venda o número indicado por quantity de unidades do vinho wine pelo valor value. Caso o wine não exista, deve ser devolvido um erro.
- view <wine>: obtém as informações associadas ao vinho identificado por wine, nomeadamente a imagem associada, a classificação média e, caso existam unidades do vinho disponíveis para venda, a indicação do utilizador que as disponibiliza, o preço e a quantidade disponível. Caso o vinho wine não exista, deve ser devolvido um erro.
- buy <wine> <seller> <quantity>: compra quantity unidades do vinho wine ao utilizador seller. O número de unidades deve ser removido da quantidade disponível e deve ser transferido o valor correspondente à compra da conta do comprador para o vendedor. Caso o vinho não exista, ou não existam unidades suficientes, ou o comprador não tenha saldo suficiente, deverá ser devolvido e assinalado o erro correspondente.
- wallet: obtém o saldo atual da carteira.
- classify <wine> <stars>: atribui ao vinho wine uma classificação de 1 a 5, indicada por stars. Caso o vinho wine não exista, deve ser devolvido um erro.
- talk <user> <message>: permite enviar uma mensagem privada ao utilizador user (por exemplo, uma pergunta relativa a um vinho à venda). Caso o utilizador não exista, deve ser devolvido um erro.
- read: permite ler as novas mensagens recebidas. Deve ser apresentada a identificação do remetente e a respectiva mensagem. As mensagens são removidas da caixa de mensagens do servidor depois de serem lidas.

O servidor mantém um arquivo com os usuários do sistema e suas respectivas senhas. As informações relativas aos vinhos e as caixas de mensagens de cada usuário também devem ser mantidas em arquivo para evitar a perda de informação no caso de uma falha do servidor.

## Autores

- Inês Esteves (56276)
- Vasco Barros (54986)
- André Pereira (56298)