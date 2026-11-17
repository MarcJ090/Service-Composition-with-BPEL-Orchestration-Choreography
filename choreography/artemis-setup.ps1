$artemis = Join-Path $env:ARTEMIS_HOME 'bin\artemis.ps1'

& $artemis address create --name EnrollmentRequested  --multicast
& $artemis address create --name StudentRegistered   --multicast
& $artemis address create --name PaymentDecided      --multicast
& $artemis address create --name EnrollmentCompleted --multicast

& $artemis start
Write-Host 'Artemis console: http://localhost:8161 (admin / admin)'
