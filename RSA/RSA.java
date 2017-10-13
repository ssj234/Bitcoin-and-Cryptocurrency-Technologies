package test;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author shisj
 *
 */
public class RSA {

	public static void main(String[] args) {
		// 1.选取两个素数 http://blog.csdn.net/linandixon/article/details/5203226  RSA-768 （768 bits, 232 digits）
		BigInteger p1 = new BigInteger("33478071698956898786044169848212690817704794983713768568912431388982883793878002287614711652531743087737814467999489");
		BigInteger p2 = new BigInteger("36746043666799590428244633799627952632279158164343087642676032283815739666511279233373417143396810270092798736308917");
		
		// 2.计算n= p1 * p2 
		// 1230186684530117755130494958384962720772853569595334792197322452151726400507263657518745202199786469389956474942774063845925192557326303453731548268507917026122142913461670429214311602221240479274737794080665351419597459856902143413
		BigInteger n = p1.multiply(p2);
		System.out.println(n);
		
		// 3.计算phi(n)
		BigInteger p1sub1 = new BigInteger("33478071698956898786044169848212690817704794983713768568912431388982883793878002287614711652531743087737814467999488");
		BigInteger p2sub1 = new BigInteger("36746043666799590428244633799627952632279158164343087642676032283815739666511279233373417143396810270092798736308916");
		BigInteger phi = p1sub1.multiply(p2sub1);
		System.out.println(phi);
		
		// 4.选取e
		BigInteger e = new BigInteger("65537");
		
		// 5.公钥是{65537,n}，下面计算了99加密后的结果为
		// 447215946244875322543716859563044890089236062419782339159830060129834659948643329666367747577098611312690600841568256926977283569938526535518264254805716730578891836351346759429118374536597270170966358780844365815376484000354495034
		BigInteger c = new BigInteger("99").modPow(e,n);
		System.out.println("99 的 65537次方 mod n 等于 :"+c);
		
		// 6.计算d  d = (k * phi(n) + 1) / e
		// d is 703813872109751212728960868893055483396831478279095442779477323396386489876250832944220079595968592852532432488202250497425262918616760886811596907743384527001944888359578241816763079495533278518938372814827410628647251148091159553
		for(int k = 1 ; k < 1000000 ; k++ ) { // 遍历取k
			if(phi.multiply(new BigInteger(String.valueOf(k))).add(new BigInteger("1")).mod(e) .compareTo(new BigInteger("0")) == 0) {
				System.out.println("d is :" + phi.multiply(new BigInteger(String.valueOf(k))).add(new BigInteger("1")).divide(e));
			}
		}
		// d =  703813872109751212728960868893055483396831478279095442779477323396386489876250832944220079595968592852532432488202250497425262918616760886811596907743384527001944888359578241816763079495533278518938372814827410628647251148091159553
		BigInteger d = new BigInteger("703813872109751212728960868893055483396831478279095442779477323396386489876250832944220079595968592852532432488202250497425262918616760886811596907743384527001944888359578241816763079495533278518938372814827410628647251148091159553");
				
		
		// 想要破解
		// 1. 对N进行因式分解，这很难需要2的768次方 很难...
		// 2. m的e次方的d次方 mod n = m 遍历d，使得等式相等，这也需要2的768次方 很难...
		BigInteger three = new BigInteger("3");
		List<String> list = new ArrayList<String>();
		
		System.out.println("密文c的d次方mod n 为明文："+c.modPow(d, n));
		
		System.out.println("开始计算d... 算了 太难了");
		for(int i=Integer.MAX_VALUE;i>0;i--){
			if(i % 10000000 == 0) {
				System.out.println("current :" + i);
			}
			BigInteger test = new BigInteger(String.valueOf(65537*i));
		   if(three.modPow(test,n).compareTo(three) == 0){
			   System.out.println("d 等于 "+i+"  c的d次方 mod n ：" + c.modPow(new BigInteger(String.valueOf(i)),n));
//			   if(nine.modPow(test,n).compareTo(nine) == 0){
//					System.out.println("d 等于 "+i+"  c的d次方 mod n ：" + c.modPow(new BigInteger(String.valueOf(i)),n));
//				}
			   list.add("d 等于 "+i+"  c的d次方 mod n ：" + c.modPow(new BigInteger(String.valueOf(i)),n));
			}
		}
		System.out.println("=====end====");
		for(String rs :list) {
			System.out.println(rs);
		}
		
		/*
		// P = 17  Q = 11  N = 187  {7,187}  {23,187}
		System.out.println("99 的 7次方 mod 187 等于 176 "+new BigInteger("99").modPow(new BigInteger("7"),new BigInteger("187")));
		BigInteger three = new BigInteger("3");
		BigInteger nine = new BigInteger("9");
		BigInteger n = new BigInteger("187");
		BigInteger c = new BigInteger("176");
		
		for(int i=1;i<200;i++){
			BigInteger test = new BigInteger(String.valueOf(7*i));
		   if(three.modPow(test,n).compareTo(three) == 0){
				if(nine.modPow(test,n).compareTo(nine) == 0){
					System.out.println("d 等于 "+i+"  176的d次方 mod 187 ：" + c.modPow(new BigInteger(String.valueOf(i)),n));
				}
			}
		}*/
		
		
	}
}
