#ifndef __BLUETOOTH__H__
#define __BLUETOOTH__H__

#include <avr/io.h>                         // AVR �⺻ include
 

void init_uart1()                               // UART1 �ʱ�ȭ �Լ�
{

    UCSR1B = 0x18;           // �۽� Transmit(TX), Receive(RX) Enable
    UCSR1C = 0x06;          // UART Mode, 8 Bit Data, No Parity, 1 Stop Bit

    UBRR1H = 0;  // Baudrate ����
    UBRR1L = 8;   // 16Mhz, 115200 baud

}


void putchar1(char c)                      // 1 ���ڸ� �۽�(Transmit)�ϴ� �Լ�
{

     while(!(UCSR1A & (1<<UDRE))) ;           // UDRE : UCSR1A 5�� ��Ʈ, UDRE�� define�� = 5
                                                                   // ��, 1�� 5�� �������� shift�� ���̹Ƿ� 0x20�� &
     UDR1 = c;                                             // 1 ���� ����, �۽� �����͸� UDR0�� ����
}

 

char getchar1()                               // 1 ���ڸ� ����(receive)�ϴ� �Լ�
{

     while (!(UCSR1A & (1<<RXC1))) ;           // UCSR1A 7�� ��Ʈ, RXC�� define �� = 7
                                                                   // ��, 1�� 7�� �������� shift�� ���̹Ƿ� 0x80�� &
     return(UDR1);                                       // 1 ���� ����, UDR1���� ���� �����͸� ������
}

#endif
