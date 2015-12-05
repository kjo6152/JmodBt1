#ifndef __BLUETOOTH__H__
#define __BLUETOOTH__H__

#include <avr/io.h>                         // AVR 기본 include
 

void init_uart1()                               // UART1 초기화 함수
{

    UCSR1B = 0x18;           // 송신 Transmit(TX), Receive(RX) Enable
    UCSR1C = 0x06;          // UART Mode, 8 Bit Data, No Parity, 1 Stop Bit

    UBRR1H = 0;  // Baudrate 세팅
    UBRR1L = 8;   // 16Mhz, 115200 baud

}


void putchar1(char c)                      // 1 문자를 송신(Transmit)하는 함수
{

     while(!(UCSR1A & (1<<UDRE))) ;           // UDRE : UCSR1A 5번 비트, UDRE의 define값 = 5
                                                                   // 즉, 1을 5번 왼쪽으로 shift한 값이므로 0x20과 &
     UDR1 = c;                                             // 1 문자 전송, 송신 데이터를 UDR0에 넣음
}

 

char getchar1()                               // 1 문자를 수신(receive)하는 함수
{

     while (!(UCSR1A & (1<<RXC1))) ;           // UCSR1A 7번 비트, RXC의 define 값 = 7
                                                                   // 즉, 1을 7번 왼쪽으로 shift한 값이므로 0x80과 &
     return(UDR1);                                       // 1 문자 수신, UDR1에서 수신 데이터를 가져옴
}

#endif
