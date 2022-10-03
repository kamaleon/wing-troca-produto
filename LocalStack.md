# Configuração do LocalStack
Para facilitar o desenvolvimento é importante deixar o ambiente autossuficiente para que o colaborador possa trabalhar sem ter que ir atrás de uma credencial da AWS. Com o LocalStack é possível simular localmente alguns serviços da AWS, como por exemplo o SQS e o S3.

### Pré-requisitos
É necessário ter instalado as seguintes ferramentas:
1. AWS CLI [[link](https://docs.aws.amazon.com/pt_br/cli/latest/userguide/install-linux.html#install-linux-bundled-sudo "link")]
2. Docker

### Setup

##### Criar um profile no CLI para comunicação com o Localstack

```shell
aws configure --profile localstack
```
Em seguida, informe os seguintes valores:

| Parâmetro  | Valor  |
| :------------ | :------------: |
| AWS Access Key ID  | **dev**  |
| AWS Secret Access Key  | **dev**  |
| Default region name  | **us-east-1**  |
| Default output format | **json** |


##### Levantar o container do LocalStack
```shell
docker-compose -f ~/kamaleon/docker/docker-compose-sqs.yml up -d
```
---
### SQS

##### Criar Fila
```shell
aws sqs create-queue --profile localstack --queue-name=storm2.fifo --attributes FifoQueue=true --endpoint-url=http://localhost:4566
```

##### Enviar uma mensagem
```shell
aws sqs send-message --profile localstack --endpoint-url=http://localhost:4566 --queue-url http://localhost:4566/000000000000/storm2.fifo --message-body 'conteúdo da mensagem'
```
##### Ler mensagem
```shell
aws sqs receive-message --profile localstack --endpoint-url=http://localhost:4566 --queue-url http://localhost:4566/000000000000/storm2.fifo
```
##### Remover mensagem
O `ReceiptHandle` é adquirido ao ler uma mensagem.
```shell
aws sqs delete-message --profile localstack --endpoint-url=http://localhost:4566 --queue-url http://localhost:4566/000000000000/storm2.fifo --receipt-handle "xxxxx"
```

##### Status da fila
```shell
aws sqs get-queue-attributes --profile localstack --endpoint-url=http://localhost:4566 --queue-url http://localhost:4566/000000000000/storm2.fifo
```
##### Deletar a Fila
```shell
aws sqs delete-queue --profile localstack --endpoint-url=http://localhost:4566 --queue-url http://localhost:4566/000000000000/storm2.fifo
```
