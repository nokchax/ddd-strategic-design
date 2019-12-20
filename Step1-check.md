## 도메인 파악 및 모델 연관관계 파악

### 9 개의 모델 존재
1. __order__
  * 아이디, 주문 테이블 아이디, 주문상태 값, 주문한 시간, 주문라인상품 아이디
2. __order_table__
  * 아이디, 테이블 그룹 아이디, 몇 명 게스트, 주문 테이블 비었는지 여부
3. __order_line_item__
  * 주문라인순서, 주문 아이디, 메뉴 아이디, 개수
4. __menu__
  * 아이디, 메뉴명, 메뉴가격, 메뉴 그룹 아이디, 메뉴상품 아이디
5. __menu_product__
  * 메뉴상품순서, 메뉴 아이디, 상품 아이디, 개수
6. __menu_group__
  * 아이디, 메뉴그룹 이름
7. __table_group__
  * 아이디, 생성시간, 주문테이블 아이디
8. __product__
  * 아이디, 이름, 가격


### 모델 간의 연관관계 파악 ( ORM 사고 )
- order -- order_table ( 1:1 단방향 관계 )
- order_line_item -- order ( N:1 양방향 관계 )
- order_line_item -- menu ( 1:1 단방향 관계 )
- menu -- menu_group ( 1:1 단방향 관계 )
- menu_product -- menu ( N:1 양방향 관계 )
- order_table -- table_group ( N:1 양방향 관계 )
