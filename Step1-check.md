## 도메인 파악 및 모델 연관관계 파악

### 9 개의 모델 존재
1. order
2. order_table
3. order_line_item
4. menu
5. menu_product
6. menu_group
7. table_group
8. product


### 모델 간의 연관관계 파악 ( ORM 사고 )
- order -- order_table ( 1:1 단방향 관계 )
- order_line_item -- order ( N:1 양방향 관계 )
- order_line_item -- menu ( 1:1 단방향 관계 )
- menu -- menu_group ( 1:1 단방향 관계 )
- menu_product -- menu ( N:1 양방향 관계 )
- order_table -- table_group ( N:1 양방향 관계 )
