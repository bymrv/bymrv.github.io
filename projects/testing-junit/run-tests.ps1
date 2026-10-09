$ErrorActionPreference = 'Stop'

if (-not $env:JAVA_HOME) {
    $javaCandidates = @(
        Get-ChildItem -Path "$env:ProgramFiles\Java\jdk*" -Directory -ErrorAction SilentlyContinue
        Get-ChildItem -Path "$env:ProgramFiles\Eclipse Adoptium\jdk*" -Directory -ErrorAction SilentlyContinue
    ) | Sort-Object Name -Descending
    $javaCandidate = $javaCandidates | Where-Object { Test-Path (Join-Path $_.FullName 'bin\javac.exe') } | Select-Object -First 1
    if ($javaCandidate) { $env:JAVA_HOME = $javaCandidate.FullName }
}

if (-not $env:JAVA_HOME -or -not (Test-Path (Join-Path $env:JAVA_HOME 'bin\javac.exe'))) {
    throw 'Necesitas un JDK 17 o posterior. Configura JAVA_HOME con su carpeta de instalación.'
}

$mavenCommand = Get-Command mvn.cmd -ErrorAction SilentlyContinue
if ($mavenCommand) {
    $mavenExecutable = $mavenCommand.Source
} else {
    $toolsDirectory = Join-Path $PSScriptRoot '.tools'
    $mavenExecutable = Join-Path $toolsDirectory 'apache-maven-3.9.9\bin\mvn.cmd'
    if (-not (Test-Path -LiteralPath $mavenExecutable)) {
        New-Item -ItemType Directory -Path $toolsDirectory -Force | Out-Null
        $mavenArchive = Join-Path $toolsDirectory 'apache-maven-3.9.9-bin.zip'
        $mavenUrl = 'https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/3.9.9/apache-maven-3.9.9-bin.zip'
        Write-Host 'Descargando Maven en la carpeta del proyecto...'
        Invoke-WebRequest -Uri $mavenUrl -OutFile $mavenArchive -UseBasicParsing
        $expectedHash = (Invoke-WebRequest -Uri "$mavenUrl.sha512" -UseBasicParsing).Content.Trim()
        $actualHash = (Get-FileHash -LiteralPath $mavenArchive -Algorithm SHA512).Hash
        if ($actualHash -ne $expectedHash) { throw 'La verificación de Maven no coincide. No se ejecutará el archivo descargado.' }
        Expand-Archive -LiteralPath $mavenArchive -DestinationPath $toolsDirectory -Force
    }
}

Push-Location $PSScriptRoot
try {
    & $mavenExecutable --batch-mode --no-transfer-progress @args test
    $testExitCode = $LASTEXITCODE
} finally {
    Pop-Location
}
exit $testExitCode
