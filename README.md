
## 기술 관련 메모

### MVC (Model-View-Controller)
**Spring MVC 는 Front Controller Pattern 을 기반으로 DispatcherServlet 이 모든 HTTP 요청을 중앙집중식으로 처리**

```text
Client          DispatcherServlet    HandlerMapping    HandlerAdapter   Controller     ViewResolver    View
  |                     |                   |               |               |               |            |
  |--1. HTTP Request--->|                   |               |               |               |            |
  |                     |                   |               |               |               |            |
  |                     |--2. 핸들러 조회----->|               |               |               |            |
  |                     |<-3. Handler 반환---|               |               |               |            |
  |                     |                   |               |               |               |            |
  |                     |--4. 실행 요청-------|-------------->|               |               |            |
  |                     |                   |               |--5. 호출------>|               |            |
  |                     |                   |               |<-6. ModelView-|               |            |
  |                     |<-7. ModelAndView--|---------------|               |               |            |
  |                     |                   |               |               |               |            |
  |                     |--8. View 검색------|---------------|---------------|-------------->|            |
  |                     |<-9. View 객체------|---------------|---------------|---------------|            |
  |                     |                   |               |               |               |            |
  |                     |--10. 렌더링 요청-----|---------------|---------------|---------------|----------->|
  |                     |<-11. HTML 응답-----|---------------|---------------|---------------|------------|
  |                     |                   |               |               |               |            |
  |<-12. HTTP Response--|                   |               |               |               |            |
  |                     |                   |               |               |               |            |
```

<br/>
<br/>

### @Transactional
**Spring은 AOP(Aspect-Oriented Programming) 를 사용하여 트랜잭션을 관리, @Transactional이 적용된 메서드 호출 시 Proxy 객체가 트랜잭션 경계를 설정**

1. 메서드 호출 → Proxy 객체 intercept
2. TransactionManager.begin() → 트랜잭션 시작
3. 데이터베이스 커넥션 획득
4. Hibernate Session 생성 + FlushMode.AUTO 설정
5. 실제 비즈니스 메서드 실행
    - 엔티티 로딩 시 1차 캐시 스냅샷 생성 (Dirty Checking 준비)
    - 변경 감지를 위한 원본 상태 보관
6. 메서드 종료 시 자동 flush 실행 (변경사항 DB 반영)
7. 성공 시: commit() / 예외 시: rollback()
8. 트랜잭션 종료 및 자원 정리

###### @Transactional(readOnly = true) 최적화 메커니즘
1. 메서드 호출 → Proxy 객체 intercept
2. TransactionManager.begin() → 읽기 전용 트랜잭션 시작
3. 데이터베이스 커넥션 획득 (읽기 전용 커넥션 풀 또는 Slave DB)
4. Hibernate Session 생성 + FlushMode.MANUAL 설정 ❗️
5. 실제 비즈니스 메서드 실행
    - 엔티티 로딩 시 1차 캐시 스냅샷 생성 생략 ❗️
    - Dirty Checking 비활성화 ❗️
6. 메서드 종료 시 flush 실행 생략 ❗️
7. commit()
8. 트랜잭션 종료 및 자원 정리

<br/>

###### Dirty Checking?
**JPA/Hibernate가 엔티티의 변경사항을 자동으로 감지하여 UPDATE 쿼리를 생성하는 메커니즘**

```java
@Transactional
public void updatePost(Long postId) {
    Post post = postRepository.findById(postId);
    post.updateTitle("새로운 제목");
}
```
- `Post post = postRepository.findById(postId);`
  - 엔티티 조회 시점 (영속성 컨텍스트에 로딩)
    - Hibernate 가 현재 상태의 '스냅샷'을 메모리에 보관
    - 스냅샷: {id: 1, title: "원래 제목", content: "원래 내용"}
- `post.updateTitle("새로운 제목");`
  - 엔티티 수정
    - 실제 객체: {id: 1, title: "새로운 제목", content: "원래 내용"}
- `트랜잭션 커밋 시점 (flush 실행)`
  - Hibernate 가 '현재 상태'와 '스냅샷'을 비교
    - title 필드 변경 감지 → 자동으로 UPDATE 쿼리 생성
    - UPDATE posts SET title = '새로운 제목' WHERE id = 1;

<br/>

###### Dirty Checking 참고: Insert, Delete 는 다른 메커니즘을 사용

```java
@Transactional
public void createPost() {
    Post newPost = new Post("새 제목", "새 내용");
    postRepository.save(newPost);
}
```
- Insert
  - 즉시 Action Queue에 등록
    - ActionQueue: [INSERT Post(title="새 제목", content="새 내용")]
    - flush 시점에 INSERT 쿼리 실행
    - INSERT INTO posts (title, content) VALUES ('새 제목', '새 내용');

<br/>

```java
@Transactional  
public void deletePost(Long postId) {
    Post post = postRepository.findById(postId);
    postRepository.delete(post);
}
```
- Delete
  - Entity 상태를 REMOVED로 변경
    - EntityState: MANAGED → REMOVED
    - flush 시점에 DELETE 쿼리 실행  
    - DELETE FROM posts WHERE id = 1;

<br/>
<br/>

### Bean Validation
**JSR-303 표준을 구현한 Hibernate Validator가 런타임에 객체 유효성을 검증, ArgumentResolver와 통합하여 자동 검증을 제공**

1. HTTP Request 수신 → DispatcherServlet
2. HandlerMapping → Controller 메서드 매핑
3. HandlerAdapter → ArgumentResolver 호출
4. ArgumentResolver → @Valid 어노테이션 감지
5. 객체 바인딩 및 타입 변환 (DataBinder)
6. Validation 실행:  
   - JSR-303 어노테이션 스캔 (@NotBlank, @Size 등)  
   - Hibernate Validator 엔진 초기화  
   - 제약 조건별 Validator 인스턴스 생성  
   - 필드별 검증 실행 (fail-fast 방식)  
   - ConstraintViolation 객체 생성 (검증 실패 시)  
7. BindingResult에 검증 결과 저장
8. Controller 메서드 실행