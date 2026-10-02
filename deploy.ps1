$ErrorActionPreference = "Stop"

# Configurações do Servidor
$KEY = "C:\Users\leosj\Documents\ibaji\ssh-key-2025-11-25.key"
$SERVER = "ubuntu@150.230.89.185"
$JAR_NAME = "voluntariado-1.0.0.jar"

Write-Host "=========================================" -ForegroundColor Green
Write-Host " Iniciando Deploy para Produção - Ibaji" -ForegroundColor Green
Write-Host "=========================================" -ForegroundColor Green

Write-Host "`n[1/4] Fazendo o build do projeto (Maven)..." -ForegroundColor Cyan
mvn clean package -DskipTests
if ($LASTEXITCODE -ne 0) {
    Write-Host "Erro no build! Abortando deploy." -ForegroundColor Red
    exit 1
}

Write-Host "`n[2/4] Enviando arquivos para o servidor (SCP)..." -ForegroundColor Cyan
# Envia os arquivos individualmente para evitar problemas de conexão
scp -i "$KEY" Dockerfile "$SERVER`:/home/ubuntu/"
scp -i "$KEY" docker-compose.prod.yml "$SERVER`:/home/ubuntu/"
scp -i "$KEY" target\$JAR_NAME "$SERVER`:/home/ubuntu/"

Write-Host "`n[3/4] Reiniciando a aplicação no servidor (SSH)..." -ForegroundColor Cyan
# Executa os comandos docker de uma vez só via SSH
ssh -i "$KEY" "$SERVER" "cd /home/ubuntu && docker compose -f docker-compose.prod.yml down && docker compose -f docker-compose.prod.yml up -d --build"

Write-Host "`n[4/4] Deploy concluído! Exibindo logs (Pressione Ctrl+C para sair)..." -ForegroundColor Green
# Abre a conexão apenas para os logs
ssh -i "$KEY" "$SERVER" "docker logs -f voluntariado-app"
