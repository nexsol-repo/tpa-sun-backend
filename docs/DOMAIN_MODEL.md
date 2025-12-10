# 도메인 모델 (Domain Model)

## 1. 회원 (Account)
시스템을 이용하는 주체(사업자)입니다.
* **식별자(ID):** 사업자번호 + 이메일주소 (복합 유니크 키)
* **주요 속성:**
    * `companyCode` (사업자등록번호): 동일 사업자번호로 여러 계정 생성 가능 (이메일로 구분)
    * `applicantEmail` (이메일): 로그인 ID 역할, 인증 수단
    * `repName` (대표자명)
    * `repPhone` (대표자 휴대폰번호)
    * `companyName` (상호/법인명)
    * `applicantName` (신청자명)
    * `applicantPhone` (신청자 휴대폰번호)

## 2. 발전소 (PowerPlant)
보험 가입의 대상이 되는 물리적 시설입니다.
* **주요 속성:**
    * `plantName` (발전소명)
    * `address` (주소): 설치 지역(전라, 경상, 제주, 그 외) 자동 매핑 필요
    * `capacity` (설비 용량): kW 단위 (BigDecimal)
    * `area` (발전소 면적): $m^2$ 단위, 미입력 시 용량 기반 산출 로직 적용
    * `inspectionDate` (사용 전 검사일)
    * `facilityType` (설비 형태): 평지, 농지, 지붕위(슬라브/판넬), 야산, 수상
        * *판별 로직:* '지붕' 포함 여부가 보험료 산출에 영향
    * `operationType` (구동 방식): 고정식, 가변형, 추적식 등
    * `powerBuyer` (전력 판매처): KEPCO, KPX

## 3. 보험 계약 (InsuranceContract)
회원이 발전소에 대해 가입하는 보험 정보입니다.
* **상태(Status):** 작성중, 심사중, 가입완료, 기간만료, 만기임박, 가입취소, 중도해지
* **주요 속성:**
    * `startDate` (보험 개시일)
    * `endDate` (보험 종료일)
    * `paymentDate` (결제일)
    * `totalPremium` (총 보험료): MD + BI + GL 합계
    * `isEssInstalled` (ESS 설치 여부): 가입 제한 조건에 영향
    * `hasAccidentHistory` (최근 5년 사고 이력): Y/N

## 4. 담보 상세 (Coverage)
보험 계약에 포함된 세부 담보 내용입니다.
* **재물손해 (MD - Material Damage)**
    * `insuranceAmount`: 가입금액 (발전소 재건설 비용)
    * `includeCivilEng`: 토목공사비 포함 여부
    * `deductible`: 자기부담금 (일반/자연)
* **기업휴지 (BI - Business Interruption)**
    * `insuranceAmount`: 가입금액 (연간 예상 발전 매출액)
    * `deductible`: 면책일수
* **배상책임 (GL - General Liability)**
    * `limitAmount`: 보상 한도액 (1억 ~ 10억 초과)
    * `deductible`: 자기부담금

## 5. 질권 (Pledge)
금융기관 대출 등을 위해 설정된 질권 정보입니다.
* **주요 속성:**
    * `bankName` (질권 은행)
    * `amount` (질권 금액)
    * `managerName` (담당자명)
    * `managerContact` (담당자 연락처)
    * `sendAddress` (증권 발송 주소)
    * `sendingStatus` (증권 송부 여부): 미송부, 송부완료, 대상아님

## 6. 사고 접수 (AccidentReport)
가입된 보험에 대해 사고를 접수하는 도메인입니다.
* **상태(Status):** 현장심사중, 사고접수완료, 지급완료
* **주요 속성:**
    * `accidentDate` (사고 일시)
    * `location` (사고 장소)
    * `accidentType` (사고 종류): 신체, 재산, 법적방어
    * `damageDescription` (피해 내용)
    * `estimatedLoss` (추정 손해액)
    * **첨부파일:** 현장사진, 견적서 등