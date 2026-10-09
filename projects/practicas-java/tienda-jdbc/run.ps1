$ErrorActionPreference = 'Stop'
$project = $PSScriptRoot

if (-not (Get-Command mvn -ErrorAction SilentlyContinue)) {
    throw 'No se encuentra Maven. Instala Maven 3.9 o posterior y vuelve a intentarlo.'
}

Push-Location $project
try {
    mvn -q compile exec:java
    if ($LASTEXITCODE -ne 0) { throw "Maven terminó con el código $LASTEXITCODE." }
} finally {
    Pop-Location
}
