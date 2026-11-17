$uri = 'http://localhost:8161/api/message/EnrollmentRequested'

$success = @{
  eventId       = [guid]::NewGuid().ToString()
  correlationId = 'corr-1001'
  timestamp     = (Get-Date).ToUniversalTime().ToString('yyyy-MM-ddTHH:mm:ssZ')
  source        = 'client'
  type          = 'EnrollmentRequested'
  data          = @{ studentId = 'STU-10345'; program = 'MSc-ISI'; amount = '350000' }
} | ConvertTo-Json -Compress -Depth 5

$refused = @{
  eventId       = [guid]::NewGuid().ToString()
  correlationId = 'corr-1002'
  timestamp     = (Get-Date).ToUniversalTime().ToString('yyyy-MM-ddTHH:mm:ssZ')
  source        = 'client'
  type          = 'EnrollmentRequested'
  data          = @{ studentId = 'STU-90001'; program = 'MSc-ISI'; amount = '900000' }
} | ConvertTo-Json -Compress -Depth 5

curl.exe -u admin:admin -H 'Content-Type: application/json' -XPOST $uri -d $success
curl.exe -u admin:admin -H 'Content-Type: application/json' -XPOST $uri -d $refused
