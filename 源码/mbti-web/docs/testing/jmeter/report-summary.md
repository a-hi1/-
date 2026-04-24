# JMeter Report Summary (Evidence)

## Test Plan
- Tool: JMeter 5.6.3
- Threads: 10 (sample evidence)
- Ramp-up: 10s
- Loop Count: 1
- Target: `http://127.0.0.1:8080/mbti-web`

## Result File
- Raw result: `docs/testing/jmeter/results.jtl`

## Summary Metrics (from sample run)
- Total requests: 8
- Success rate: 100%
- Average elapsed: 446 ms
- Max elapsed: 720 ms

## Screenshot Placeholder
- Save JMeter dashboard screenshot as:
  - `docs/testing/jmeter/report-screenshot.png`
- Recommended screenshot content:
  - APDEX
  - Response Time Over Time
  - Throughput
  - Error %

## Generate HTML Report Command
```powershell
jmeter -g .\docs\testing\jmeter\results.jtl -o .\docs\testing\jmeter\html-report
```
