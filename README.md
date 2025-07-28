# CS DeepDive

Java 기반으로 컴퓨터공학 핵심 개념을 직접 구현한 학습형 프로젝트입니다.  
자료구조, 알고리즘, 운영체제, 네트워크, 보안, 게임이론 등 전산학 전반에 걸친 주제를 코드로 표현하며 내부 동작 원리를 심도 있게 이해하고자 합니다.


## 📁 프로젝트 구조

| 대분류             | 소분류              | 구현 항목/파일                         | 상태     |
|--------------------|----------------------|----------------------------------------|----------|
| **DataStructure**  | **Linear**           |                                        | ✅ 완료  |
|                    |                      | **LinkedList**                         | ✅ 완료  |
|                    |                      | ├─ CircularDoublyLinkedList.java       | ✅       |
|                    |                      | ├─ DoublyLinkedList.java               | ✅       |
|                    |                      | ├─ DummyNodeLinkedList.java            | ✅       |
|                    |                      | └─ SimpleLinkedList.java               | ✅       |
|                    |                      | **Stack**                              | ✅ 완료  |
|                    |                      | ├─ ArrayStack.java                     | ✅       |
|                    |                      | └─ LinkedListStack.java                | ✅       |
|                    | **NonLinear**        |                                        | ✅ 완료  |
|                    |                      | **B-Tree**                             | ✅       |
|                    |                      | **Segment Tree**                       | ✅       |
|                    |                      | **Hash Table**                         | ☐ 예정  |
| **Algorithm**      | **Shortest Path**    | **Dijkstra**                           | ☐ 예정  |
|                    |                      | **BellmanFord**                        | ☐ 예정  |
|                    |                      | **A\***                                | ☐ 예정  |
|                    | **Sorting**          |                                        | ☐ 예정  |
| **System**         | **JVM**              | **GarbageCollector**                   | ☐ 예정  |
|                    |                      | ├─ G1                                  | ☐       |
|                    |                      | └─ CMS                                 | ☐       |
|                    | **OS**               | **Process Schedule**                   | ☐ 예정  |
|                    |                      | ├─ Round-Robin                         | ☐ 예정  |
|                    |                      | └─ MLFQ                                | ☐ 예정  |
| **Network**        | -                    | **DHCP**                               | ✅       |
|                    |                      | ├─ Discover                            | ✅       |
|                    |                      | ├─ Offer                               | ✅       |
|                    |                      | ├─ Request                             | ✅       |
|                    |                      | └─ Acknowledge (ACK)                   | ✅       |
|                    |                      | **TCP (3-way, 4-way handshaking)**     | 🔧       |
|                    |                      | **NAT**                                | 🔧       |
|                    |                      | **ARP**                                | ☐ 예정  |
|                    |                      | **DNS**                                | ☐ 예정  |
| **Security**       | -                    | **RSA**                                | ☐ 예정  |
|                    |                      | **DiffieHellman**                      | ☐ 예정  |
|                    |                      | **AES**                                | ☐ 예정  |
| **GameTheory**     | -                    | **Tit-for-Tat**                        | ☐ 예정  |


## 🛠️ 목표

- 단순 사용이 아닌 직접 구현을 통해 근본적인 작동 원리 학습
- 컴퓨터공학 핵심 개념을 하나의 레포지토리로 정리
- 기술 면접/코딩 테스트 대비 실습 자료로 활용
