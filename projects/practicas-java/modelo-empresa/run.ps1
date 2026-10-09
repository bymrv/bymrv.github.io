$ErrorActionPreference = 'Stop'
$project = $PSScriptRoot
$build = Join-Path $project 'build'
$sources = Get-ChildItem -Path (Join-Path $project 'src/main/java') -Filter '*.java' -Recurse | ForEach-Object { $_.FullName }
$oldInputEncoding = [Console]::InputEncoding
$oldOutputEncoding = [Console]::OutputEncoding
$oldPsEncoding = $OutputEncoding
$utf8 = [System.Text.UTF8Encoding]::new()

if (-not (Get-Command javac -ErrorAction SilentlyContinue)) {
    throw 'No se encuentra javac. Instala un JDK 17 o posterior y vuelve a intentarlo.'
}

New-Item -ItemType Directory -Force -Path $build | Out-Null
[Console]::InputEncoding = $utf8
[Console]::OutputEncoding = $utf8
$OutputEncoding = $utf8
try {
    javac -encoding UTF-8 -d $build $sources
    if ($LASTEXITCODE -ne 0) { throw "No se pudo compilar el modelo de empresa (código $LASTEXITCODE)." }
    java -cp $build portfolio.nf2.DemoEmpresa
} finally {
    [Console]::InputEncoding = $oldInputEncoding
    [Console]::OutputEncoding = $oldOutputEncoding
    $OutputEncoding = $oldPsEncoding
}
