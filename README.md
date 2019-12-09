# 키친포스

## 요구 사항

- 주문
    - [] 주문은 테이블 별로 이루어진다.
    - [] 주문은 고유의 ID와 주문 정보를 포함한다.
    - [] 주문 정보는 주문한 주문 항목들을 포함한다.
    - [] 주문 항목에는 주문 메뉴와 갯수가 있어야 한다.
    - [] 주문을 하면 주문시간과 주문 상태가 변경된다.
    - [] 주문 상태는 조리중, 먹는중(?), 완료 상태로 변경할 수 있다.
    - [] 주문 목록을 볼 수 있다.
    - [] 새로운 주문을 추가 할 수 있다.
- 메뉴 그룹
    - [] 메뉴 그룹 목록을 볼 수 있다.
    - [] 새로운 메뉴 그룹을 추가할 수 있다.
- 메뉴
    - [] 메뉴는 메뉴 그룹에 포함 되어야 한다.
    - [] 메뉴는 고유의 ID, 이름, 가격, 메뉴 그룹 ID를 포함한다.
    - [] 메뉴 가격은 0보다 커야 한다.
    - [] 메뉴는 다수의 상품을 포함할 수 있다.
    - [] 메뉴 목록을 볼 수 있다.
    - [] 새로운 메뉴를 추가할 수 있다.
- 상품
    - [] 상품은 고유의 ID, 상품 이름, 가격이 있다.
    - [] 상품 가격은 0보다 커야 한다.
    - [] 새로운 상품을 추가할 수 있다.
    - [] 상품 목록을 볼 수 있다.
- 테이블 그룹
    - [] 새로운 테이블 그룹을 추가할 수 있다.
    - [] 테이블 그룹을 삭제할 수 있다.
- 테이블
    - [] 테이블은 고유의 ID, 테이블에 앉은 손님 수, 테이블 상태 (비어있는지) 정보를 가지고 있다.
    - [] 손님이 도착하면 테이블에 앉은 손님의 수와 테이블 상태를 변경한다.
    - [] 테이블 목록을 볼 수 있다.


## 용어 사전

| 한글명 | 영문명 | 설명 |
| --- | --- | --- |

## 모델링

- 
