$ErrorActionPreference = 'Stop'
$project = $PSScriptRoot
$build = Join-Path $project 'build'
$source = Join-Path $project 'src/main/java/portfolio/nf3/CalculadoraSwing.java'

if (-not (Get-Command javac -ErrorAction SilentlyContinue)) {
    throw 'No se encuentra javac. Instala un JDK 17 o posterior y vuelve a intentarlo.'
}

New-Item -ItemType Directory -Force -Path $build | Out-Null
javac -encoding UTF-8 -d $build $source
if ($LASTEXITCODE -ne 0) { throw "No se pudo compilar la calculadora Swing (código $LASTEXITCODE)." }
Push-Location $project
try {
    java -cp $build portfolio.nf3.CalculadoraSwing
} finally {
    Pop-Location
}
