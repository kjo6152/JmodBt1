#include <avr/io.h>
#include <util/delay.h>
#include <avr/interrupt.h>
#include "bluetooth.h"
#include "led_adc.h"

#define		FALSE			0
#define		TRUE			1

//������ ���� DEFINE
//OFF / 1 / 2 / 3 / AUTO�� �� 5���� ����
#define 	STATE_OFF		0
#define 	STATE_ON_1		1
#define 	STATE_ON_2		2
#define 	STATE_ON_3		3
#define 	STATE_AUTO		4

//��ư�� ����Ʈ���̳� Ŷ���κ��� Ŭ���Ǿ����� Ȯ���ϴ� ����
volatile int isButtonClicked = FALSE;
//���� LED ���¸� �����ϴ� ����
volatile int ledState = STATE_OFF;
//�� ���� LED�� �Ѿ��ϴ��� �����ϴ� ����
volatile int ledCount = 0;

//������ ���� ���·� ����
void setNextState(){
	ledState = (ledState+1)%5;
}

//��ư�� Ŭ���Ǿ��� �� ����Ǵ� �Լ�, ������ ���� ���·� �����Ѵ�.
void onButtonClicked(){
	setNextState();
}

//����ġ �ʱ�ȭ
void init_switch(){
	DDRE = 0xcf; // 1100 1111, INT 4, 5
	EICRB = 0x0a; // 0000 1010 INT 4, 5 => falling
	EIMSK = 0x30; // 0011 0000 INT 4, 5
	sei(); // SREG |= 1<<7; // 1000 0000
}

//����ġ1
SIGNAL(SIG_INTERRUPT4){
	isButtonClicked = TRUE;
}

//����ġ2
SIGNAL(SIG_INTERRUPT5){
	isButtonClicked = TRUE;

}

//�ʱ�ȭ �Լ�
void init(){
	//�������� �ʱ�ȭ
	init_adc();
	//LED �ʱ�ȭ
	init_led();
	//����ġ �ʱ�ȭ
	init_switch();
	//������� �ʱ�ȭ
	init_bluetooth();		
}

/**
 * ��������� ����ġ�� ����� ������ �� ������ ó���� �ϴ� �Լ�
 */
void run(){


	while(1)                       
    {
		//��������κ��� ��ư �Է��� �Դ��� Ȯ���Ѵ�.
		isButtonClicked |= checkBluetooth();		
		
		//����ġ�� ��������κ��� ��ư�� �ԷµǾ����� ���¸� �����Ѵ�.
		if(isButtonClicked){
			setNextState();
			isButtonClicked = FALSE;
		}

		//AUTO����� ��� �� LED�� ������ ���Ѵ�.
		if(ledState==STATE_AUTO){
			ledCount = get_auto();
		}
		else {
			ledCount = ledState;
		}

		//LED�� �Ҵ�.
		setKitLed(ledCount);
		setLight(ledCount); 
    }

}

int main()
{
	//����̽� �ʱ�ȭ
    init();
    //�۾� ����
 	run();

}
