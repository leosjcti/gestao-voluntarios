# Sistema de Gestão de Voluntariado - Ibaji

Sistema web completo para gestão de inscrições de voluntários. O projeto permite o cadastro de dados pessoais, escolha de ministérios, aceitação de termos e upload de atestados de antecedentes criminais para armazenamento seguro em Object Storage (S3). Conta também com uma área administrativa protegida para gestão dos inscritos.

## 🚀 Tecnologias Utilizadas

* **Java 17**
* **Spring Boot 3.2.0** (Web, Data JPA, Security, Validation, Thymeleaf)
* **PostgreSQL** (Banco de dados relacional)
* **AWS SDK v2** (Integração com S3)
* **Docker & Docker Compose** (Infraestrutura local)
* **LocalStack** (Simulação da AWS S3 localmente)

---

## 📋 Pré-requisitos

Para rodar este projeto, certifique-se de ter instalado:

* [Java JDK 17+](https://adoptium.net/)
* [Maven](https://maven.apache.org/)
* [Docker](https://www.docker.com/) e Docker Compose

---

## 🛠️ Instalação e Execução (Passo a Passo)

Siga a ordem abaixo para subir a infraestrutura e a aplicação corretamente.

### 1. Subir a Infraestrutura (Docker)
Na raiz do projeto (onde está o arquivo `docker-compose.yml`), execute:

```bash
docker-compose up -d
```
Isso iniciará os containers do PostgreSQL e do LocalStack.


### 2. Configurar o Bucket S3 (LocalStack)
O LocalStack simula a AWS, mas não persiste a criação do bucket ao ser reiniciado. Sempre que subir o Docker, execute este comando para criar o bucket:

```bash
docker exec -it localstack_voluntariado awslocal s3 mb s3://voluntariado-antecedentes-bucket
```


### 3. Popular o Banco de Dados (Ministérios)
Para que o formulário exiba as opções de ministérios, insira os dados iniciais no banco:

```bash
docker exec -it postgres_voluntariado psql -U voluntario_user -d voluntariado_db -c "INSERT INTO ministerios (nome, descricao) VALUES ('Louvor', 'Equipe de música'), ('Infantil', 'Cuidado das crianças'), ('Recepção', 'Acolhimento'), ('Mídia', 'Transmissão e Som');"
```

### 4. Executar a Aplicação
Inicie o Spring Boot ativando o perfil localstack (que aponta para o Docker em vez da AWS real):
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=localstack
```

### 🔗 Acessando o Sistema
📝 Formulário de Inscrição (Público)
Acesse pelo navegador:

URL: http://localhost:8080/voluntarios/novo

🔐 Painel Administrativo (Restrito)
Área para consultar inscritos e baixar arquivos.

URL: http://localhost:8080/admin

Usuário: admin

Senha: admin123

📱 Acessando via Celular (Rede Local)
Para testar o formulário no celular:

Conecte o celular no mesmo Wi-Fi do computador.

Descubra o IP do seu computador (No terminal: ipconfig ou ifconfig).

Desative temporariamente o Firewall do Windows/Linux se não conectar.

Acesse no celular: http://[SEU_IP]:8080/voluntarios/novo


### ⚙️ Comandos de Verificação e Debug
Listar arquivos salvos no S3 Local:

```bash
docker exec -it localstack_voluntariado awslocal s3 ls s3://voluntariado-antecedentes-bucket --recursive
Consultar voluntários no Banco de Dados:

docker exec -it postgres_voluntariado psql -U voluntario_user -d voluntariado_db -c "SELECT * FROM

```