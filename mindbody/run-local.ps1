# Carga las variables del .env y levanta la app con perfil local
Get-Content .env | ForEach-Object {
    if ($_ -match '^([^#\s][^=]*)=(.*)$') {
        [System.Environment]::SetEnvironmentVariable($Matches[1], $Matches[2], 'Process')
    }
}
./mvnw spring-boot:run "-Dspring-boot.run.profiles=local"
