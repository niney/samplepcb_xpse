# 인증/인가 (security)

## Purpose

이 모듈은 samplepcb_xpse 프로젝트의 **JWT 기반 무상태(stateless) 인증/인가** 계층을 담당한다. 외부 시스템(G5 등)에서 발급된 JWT 토큰을 검증하고, 토큰 내 클레임(claims)으로부터 사용자 정보(회원 ID, 이름, 등급, 회원번호)를 추출하여 Spring Security 컨텍스트에 주입한다. 세션을 사용하지 않으며(`STATELESS`), 토큰 발급 자체는 이 서비스에서 수행하지 않고 **검증 전용**으로 동작한다.

주요 목표:
- 메서드/클래스 단위의 선언적 인증 강제 (`@JwtAuth` 어노테이션)
- `@AuthenticationPrincipal` 파라미터 감지를 통한 자동 인증 요구
- 회원 등급(`mbLevel`)에 따른 역할(ROLE) 부여 및 권한 분리

## Architecture

### 핵심 구성요소

```
요청 흐름:

HTTP Request
  |
  v
JwtAuthenticationFilter (OncePerRequestFilter)
  |-- resolveToken(): Authorization 헤더에서 Bearer 토큰 추출
  |-- JwtTokenProvider.validateToken(): HMAC-SHA 서명 검증
  |-- JwtTokenProvider.getAuthentication(): Claims -> JwtUserPrincipal + 권한 생성
  |-- SecurityContextHolder에 Authentication 설정
  |
  |-- requiresAuth(): 대상 핸들러에 @JwtAuth 또는 @AuthenticationPrincipal 존재 여부 확인
  |     |-- 있으면서 인증 없음 -> 401 Unauthorized 즉시 반환
  |     |-- 없으면 -> 인증 없이 통과 허용
  |
  v
Controller 메서드 실행
```

### 클래스별 역할

| 클래스 | 패키지 | 역할 |
|--------|--------|------|
| `JwtTokenProvider` | `security` | JWT 토큰 검증 및 `Authentication` 객체 생성. `@PostConstruct`에서 `ApplicationProperties.jwt.secret`으로 `SecretKey` 초기화 |
| `JwtAuthenticationFilter` | `security` | `OncePerRequestFilter` 구현체. 요청마다 토큰 추출/검증/컨텍스트 설정 수행. 핸들러 매핑을 통한 인증 필요 여부 동적 판단 |
| `JwtAuth` | `security` | 커스텀 어노테이션. 메서드 또는 클래스 레벨에 부착하여 JWT 인증을 필수로 지정 |
| `JwtUserPrincipal` | `security` | 인증된 사용자 정보를 담는 불변 POJO. `sub`, `mbName`, `mbLevel`, `mbNo` 필드 보유 |
| `SecurityConfig` | `config` | Spring Security 필터 체인 구성. CSRF 비활성화, STATELESS 세션, CORS, JWT 필터 등록 |
| `ApplicationProperties` | `config` | `application.jwt.secret` 및 `application.cors.*` 설정값을 타입 안전하게 바인딩 |
| `WebConfigurer` | `config` | `WebMvcConfigurer` 구현체 (현재 빈 구현, 향후 확장 지점) |

### 인증 판단 메커니즘 (`requiresAuth`)

`JwtAuthenticationFilter.requiresAuth()` 메서드는 Spring MVC의 `HandlerMapping` 목록을 순회하여 현재 요청에 매핑된 핸들러를 찾고, 다음 세 가지 조건 중 하나라도 충족되면 인증 필수로 판단한다:

1. **핸들러 메서드**에 `@JwtAuth` 어노테이션이 있는 경우
2. **핸들러 클래스**(컨트롤러)에 `@JwtAuth` 어노테이션이 있는 경우
3. 메서드 파라미터 중 `@AuthenticationPrincipal`이 붙어있고 타입이 `JwtUserPrincipal`인 경우

이 방식은 Spring Security의 전역 `authorizeHttpRequests` 설정이 `anyRequest().permitAll()`로 되어 있어도, 어노테이션 기반으로 개별 엔드포인트에 인증을 강제할 수 있게 한다.

### 권한(Role) 부여 로직

`JwtTokenProvider.getAuthentication()` 메서드에서 토큰 클레임의 `mbLevel` 값을 기준으로 권한을 부여한다:

- 모든 인증된 사용자: `ROLE_USER`
- `mbLevel >= 10`: `ROLE_USER` + `ROLE_ADMIN`

## Talks To

| 대상 | 방향 | 설명 |
|------|------|------|
| `ApplicationProperties` | 읽기 | `application.jwt.secret`에서 HMAC-SHA 시크릿 키 로드, `application.cors.*`에서 CORS 설정 로드 |
| Spring Security `SecurityContextHolder` | 쓰기 | 인증 성공 시 `Authentication` 객체를 컨텍스트에 설정 |
| Spring MVC `HandlerMapping` | 읽기 | 요청별 핸들러 메서드/클래스의 어노테이션을 검사하여 인증 필요 여부 판단 |
| `G5ShopOrderResource` | 소비자 | 모든 엔드포인트에 `@JwtAuth` 적용 (주문 검색, 상세 조회) |
| `SpEstimateResource` | 소비자 | 견적서 CRUD 및 협력사 견적 관리. `@AuthenticationPrincipal`로 `mbLevel` 기반 데이터 필터링 수행 |
| `SpPartnerOrderResource` | 소비자 | 협력사 발주 관리. `mbLevel != 10`인 경우 자신의 `mbNo`로 데이터 필터링 |
| `SpItemResource` | 소비자 | 아이템 생성/수정. `principal.getSub()`로 소유자 식별 |
| `SpBomDocumentResource` | 소비자 | BOM 문서 CRUD. `principal.getSub()`로 회원별 문서 격리 |
| `SpPartnerEstimateResource` | 소비자 | 협력사 견적 관리 |
| `HomeResource` | 소비자 | `/api/auth-test` 엔드포인트에서 `@AuthenticationPrincipal`로 인증 테스트 |

## API Surface

### 인증이 필요한 엔드포인트 (6개 컨트롤러)

**`HomeResource`**
| 메서드 | 경로 | 인증 근거 |
|--------|------|-----------|
| `GET` | `/api/auth-test` | `@AuthenticationPrincipal JwtUserPrincipal` |

**`SpItemResource`** (`/api/spItems`)
| 메서드 | 경로 | 인증 근거 |
|--------|------|-----------|
| `POST` | `/api/spItems` | `@JwtAuth` + `@AuthenticationPrincipal` |
| `GET` | `/api/spItems/{itId}` | `@JwtAuth` |
| `POST` | `/api/spItems/{itId}` | `@JwtAuth` |

**`SpEstimateResource`** (`/api/spEstimates`)
| 메서드 | 경로 | 인증 근거 |
|--------|------|-----------|
| `POST` | `/api/spEstimates` | `@JwtAuth` + `@AuthenticationPrincipal` |
| `GET` | `/api/spEstimates/{id}` | `@JwtAuth` |
| `GET` | `/api/spEstimates/byItId/{itId}` | `@JwtAuth` |
| `GET` | `/api/spEstimates/_search` | `@JwtAuth` |
| `DELETE` | `/api/spEstimates/{id}` | `@JwtAuth` |
| `POST` | `/api/spEstimates/{id}/status` | `@JwtAuth` |
| `GET` | `/api/spEstimates/{estimateItemId}/partnerEstimateItems` | `@JwtAuth` |
| `POST` | `/api/spEstimates/{estimateItemId}/partnerEstimateItems` | `@JwtAuth` |
| `POST` | `/api/spEstimates/items/{estimateItemId}/selectPartner` | `@JwtAuth` |
| `POST` | `/api/spEstimates/items/_batch/selectPartner` | `@JwtAuth` |
| `GET` | `/api/spEstimates/_searchWithPartners` | `@JwtAuth` + `@AuthenticationPrincipal` |

**`SpPartnerOrderResource`** (`/api/spPartnerOrders`)
| 메서드 | 경로 | 인증 근거 |
|--------|------|-----------|
| `GET` | `/api/spPartnerOrders/_search` | `@JwtAuth` |
| `GET` | `/api/spPartnerOrders/_searchWithPartnerOrders` | `@JwtAuth` + `@AuthenticationPrincipal` |
| `GET` | `/api/spPartnerOrders/{orderDocId}` | `@JwtAuth` |
| `GET` | `/api/spPartnerOrders/byItId/{itId}` | `@JwtAuth` + `@AuthenticationPrincipal` |
| `POST` | `/api/spPartnerOrders/_batch` | `@JwtAuth` |

**`SpBomDocumentResource`** (`/api/spBomDocuments`)
| 메서드 | 경로 | 인증 근거 |
|--------|------|-----------|
| `GET` | `/api/spBomDocuments/_search` | `@JwtAuth` + `@AuthenticationPrincipal` |
| `GET` | `/api/spBomDocuments/{id}` | `@JwtAuth` + `@AuthenticationPrincipal` |
| `POST` | `/api/spBomDocuments` | `@JwtAuth` + `@AuthenticationPrincipal` |
| `DELETE` | `/api/spBomDocuments/{id}` | `@JwtAuth` + `@AuthenticationPrincipal` |

**`G5ShopOrderResource`** (`/api/g5ShopOrders`)
- 모든 엔드포인트에 `@JwtAuth` 적용

### 인증 불필요 엔드포인트

- `GET /` - 헬스체크 (`HomeResource.home()`)
- `GET /swagger-ui/**`, `/swagger-ui.html`, `/v3/api-docs/**` - Swagger UI 및 OpenAPI 문서
- `@JwtAuth` 또는 `@AuthenticationPrincipal JwtUserPrincipal`이 없는 모든 엔드포인트

### 인증 실패 응답

인증이 필요한 엔드포인트에 유효한 토큰 없이 접근하면:

```
HTTP 401 Unauthorized
Content-Type: application/json;charset=UTF-8

{"error":"Unauthorized","message":"JWT token is required"}
```

## Data

### JWT 토큰 클레임 구조

| 클레임 | 타입 | `JwtUserPrincipal` 필드 | 설명 |
|--------|------|-------------------------|------|
| `sub` | `String` | `sub` | 사용자 식별자 (subject) |
| `mbName` | `String` | `mbName` | 회원 이름 (없으면 빈 문자열) |
| `mbLevel` | `Integer` | `mbLevel` | 회원 등급 (없으면 0, 10 이상이면 관리자) |
| `mbNo` | `Long` | `mbNo` | 회원 번호 (없으면 0L) |

### 설정 프로퍼티

`application.yaml` 내 `application` 접두사 하위 설정:

```yaml
application:
    cors:
        allowed-origins: "*"
        allowed-methods: GET, PUT, POST, DELETE, OPTIONS
        allowed-headers: "*"
        exposed-headers: "authorization, content-type, date, connection, transfer-encoding, x-application-context, x-content-type-options, x-xss-protection"
        max-age: 1800
    jwt:
        secret: "change-this-to-your-shared-secret-key-base64-encoded"
```

- `application.jwt.secret`: HMAC-SHA 서명 검증에 사용되는 비밀 키. `Keys.hmacShaKeyFor()`로 `SecretKey` 객체로 변환됨
- `application.cors.*`: `CorsConfiguration` 객체에 직접 바인딩되며, `/api/**` 경로에만 적용됨

## Key Decisions

1. **토큰 검증 전용 설계**: 이 서비스는 JWT 토큰을 **발급하지 않는다**. 토큰 발급은 외부 시스템(G5 등)에서 수행하며, 이 서비스는 공유 시크릿(`application.jwt.secret`)을 사용해 서명을 검증만 한다. 즉, 여러 서비스 간 JWT 시크릿을 공유하는 아키텍처이다.

2. **어노테이션 기반 선택적 인증**: Spring Security의 `authorizeHttpRequests`에서 `anyRequest().permitAll()`로 설정하되, `@JwtAuth` 커스텀 어노테이션과 `@AuthenticationPrincipal` 감지를 통해 필터 레벨에서 인증을 강제한다. 이는 기본적으로 모든 엔드포인트를 공개하고, 필요한 곳에만 명시적으로 인증을 요구하는 **opt-in** 방식이다.

3. **핸들러 매핑 기반 동적 판단**: `requiresAuth()` 메서드가 `HandlerMapping` 목록을 순회하여 런타임에 핸들러 어노테이션을 검사한다. URL 패턴 매칭이 아닌 실제 핸들러 메타데이터를 기반으로 판단하므로, 새로운 엔드포인트 추가 시 URL 패턴 목록을 관리할 필요가 없다.

4. **CSRF 비활성화 + STATELESS 세션**: REST API 서버로서 CSRF 보호가 불필요하며, 세션을 사용하지 않는 완전한 무상태 설계를 채택했다.

5. **등급 기반 역할 부여**: `mbLevel >= 10`이면 `ROLE_ADMIN`을 부여한다. 컨트롤러 레벨에서는 `mbLevel == 10` 직접 비교로 관리자 여부를 판단하여 데이터 필터링에 활용한다 (`SpEstimateResource`, `SpPartnerOrderResource`에서 관리자가 아니면 자신의 `mbNo`로 결과 제한).

6. **CORS 설정 외부화**: `ApplicationProperties`의 내부 클래스가 아닌 Spring의 `CorsConfiguration` 객체를 직접 `application.cors.*`에 바인딩하여 CORS 정책을 YAML로 관리한다.

## Gotchas

1. **기본 시크릿 키 위험**: `application.yaml`의 기본 `jwt.secret` 값이 `"change-this-to-your-shared-secret-key-base64-encoded"`라는 플레이스홀더 문자열이다. 프로덕션 환경에서 반드시 환경별 YAML(`application-prod.yaml` 등)이나 환경 변수로 실제 시크릿을 주입해야 한다.

2. **CORS 와일드카드**: 기본 CORS 설정에서 `allowed-origins: "*"`, `allowed-headers: "*"`로 되어 있어 모든 출처에서의 요청을 허용한다. 프로덕션 환경에서는 특정 도메인으로 제한해야 한다.

3. **토큰 만료 검증 미노출**: `JwtTokenProvider.validateToken()`은 `parseSignedClaims()`를 호출하며, 이 메서드는 JJWT 라이브러리가 내부적으로 `exp` 클레임에 의한 만료를 자동 검증한다. 그러나 토큰에 `exp` 클레임이 없는 경우에는 만료 검증이 이루어지지 않는다.

4. **requiresAuth 예외 무시**: `requiresAuth()` 메서드에서 `HandlerMapping.getHandler()` 호출 시 발생하는 모든 예외를 `catch (Exception ignored)`로 무시하고 `false`를 반환한다. 핸들러 매핑 오류가 발생해도 인증 없이 요청이 통과될 수 있으므로, 보안적으로 주의가 필요하다.

5. **mbLevel 비교 불일치**: `JwtTokenProvider`에서는 `mbLevel >= 10`이면 `ROLE_ADMIN`을 부여하지만, 컨트롤러에서는 `mbLevel != 10` 또는 `mbLevel == 10`으로 정확히 10인지를 비교한다. 만약 향후 10 이상의 다른 관리자 등급이 추가되면 컨트롤러 로직과 불일치가 발생할 수 있다.

6. **WebConfigurer 빈 구현**: `WebConfigurer`는 `WebMvcConfigurer`를 구현하지만 현재 아무런 오버라이드 메서드가 없다. CORS 설정은 `SecurityConfig.corsConfigurationSource()`에서 처리되며, `WebConfigurer`는 향후 확장 지점으로만 존재한다.

7. **인증 테스트 엔드포인트 노출**: `/api/auth-test`는 `@JwtAuth` 없이 `@AuthenticationPrincipal`만으로 인증이 강제되며, 인증 성공 시 사용자의 `sub`, `mbName`, `mbLevel`, `mbNo`를 그대로 반환한다. 프로덕션에서 이 엔드포인트가 불필요한 정보를 노출할 수 있다.

## Sources

| 파일 | 경로 |
|------|------|
| `JwtTokenProvider.java` | `src/main/java/kr/co/samplepcb/xpse/security/JwtTokenProvider.java` |
| `JwtAuthenticationFilter.java` | `src/main/java/kr/co/samplepcb/xpse/security/JwtAuthenticationFilter.java` |
| `JwtAuth.java` | `src/main/java/kr/co/samplepcb/xpse/security/JwtAuth.java` |
| `JwtUserPrincipal.java` | `src/main/java/kr/co/samplepcb/xpse/security/JwtUserPrincipal.java` |
| `SecurityConfig.java` | `src/main/java/kr/co/samplepcb/xpse/config/SecurityConfig.java` |
| `WebConfigurer.java` | `src/main/java/kr/co/samplepcb/xpse/config/WebConfigurer.java` |
| `ApplicationProperties.java` | `src/main/java/kr/co/samplepcb/xpse/config/ApplicationProperties.java` |
| `application.yaml` | `src/main/resources/application.yaml` |
| `HomeResource.java` | `src/main/java/kr/co/samplepcb/xpse/resource/HomeResource.java` |
| `SpItemResource.java` | `src/main/java/kr/co/samplepcb/xpse/resource/SpItemResource.java` |
| `SpEstimateResource.java` | `src/main/java/kr/co/samplepcb/xpse/resource/SpEstimateResource.java` |
| `SpPartnerOrderResource.java` | `src/main/java/kr/co/samplepcb/xpse/resource/SpPartnerOrderResource.java` |
| `SpBomDocumentResource.java` | `src/main/java/kr/co/samplepcb/xpse/resource/SpBomDocumentResource.java` |
