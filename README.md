# wing
Segunda etapa do Orquestrador. Lê um item da fila e realiza o processo na plataforma da Conductor.

### Instruções para deploy

##### build na aplicação
```shell
mvn clean package
```

##### docker image
```shell
docker build -t kamaleon/wing:1.0 .
```

##### variáveis de ambiente do container

Geral

| Parâmetro  | Obrigatório | Descrição  |
| :------------ | :------------: | :------------ |
| SPRING_PROFILES_ACTIVE | **sim** | Properties referente ao ambiente onde a aplicação vai rodar. Pode ser `homologacao` ou `producao` |
| KAMALEON_JWT_KEY | **sim** | Chave de criptografia do JWT |

AWS

| Parâmetro  | Obrigatório | Descrição  |
| :------------ | :------------: | :------------ |
| AMAZON_ACCESSKEY | **sim** | Access key para acessar o SQS |
| AMAZON_SECRETKEY | **sim** | Secret key para acessar o SQS |
| AMAZON_REGION | não | Região da infra na AWS. Default: `us-east-1` |
| AMAZON_SQS_BASEURL | **sim** | Base URL da fila no SQS (sem o nome da fila). Ex: `https://sqs.us-east-1.amazonaws.com/12345/` |
| AMAZON_SQS_THREADPOOLSIZE | não | Pool size da conexão com o SQS. Default: `10` |

PostgreSQL

| Parâmetro  | Obrigatório | Descrição  |
| :------------ | :------------: | :------------ |
| SPRING_DATASOURCE_URL | **sim** | URL de conexão com o banco no formato JDBC. Ex. `jdbc:postgresql://localhost:5432/wing` |
| SPRING_DATASOURCE_USERNAME | **sim** | Usuário para conectar ao banco |
| SPRING_DATASOURCE_PASSWORD | **sim** | Senha do usuário para conectar ao banco |