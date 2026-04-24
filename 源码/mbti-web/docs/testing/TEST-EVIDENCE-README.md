# Testing Evidence Package

This folder is prepared for chapter 5 testing evidence.

## 1) Backend Unit Tests
- Location: `src/test/java/com/mbti/web/util/`
- Included tests:
  - `PasswordUtilTest`
  - `MbtiScoringTest`
  - `MbtiProfilesTest`

Run command:
```powershell
mvn -f .\pom.xml test
```

## 2) Postman Collection
- File: `docs/testing/postman/MBTI-API-Collection.postman_collection.json`
- Import into Postman and run by folder/collection runner.

## 3) JMeter Evidence
- Raw results: `docs/testing/jmeter/results.jtl`
- Report summary: `docs/testing/jmeter/report-summary.md`

Optional HTML report generation:
```powershell
jmeter -g .\docs\testing\jmeter\results.jtl -o .\docs\testing\jmeter\html-report
```
