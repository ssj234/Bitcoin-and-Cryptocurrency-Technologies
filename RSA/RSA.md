# RSA


* RSA密钥一般是1024位bit，重要场合则为2048位bit。
* 主要原理：对极大整数做因数分解的难度决定了RSA算法的可靠性
* 主要过程：生成密钥，加密，解密

## 主要原理

### 欧拉函数

定义：对于正整数n来说，欧拉函数是小于n的数中与n互质整数的数目(公约数只有1的两个整数，叫做互质整数 )即：  
φ(n) = count(小于n且于n互质的整数)  
例如：φ(8) = count(1|2|3|4|5|6|7中选1|3|5|7) = 4 
φ(7) = count(1-6) = 6  
若n为质数，说明1～n-1均于n互质，则φ(n) = n - 1 (n 为质数)

### 欧拉定理

如果两个正整数a和n互质，则n的欧拉函数 φ(n) 可以让下面的等式成立：  
a^φ(n) mod n = 1  
a的φ(n)次方被n除的余数为1。或者说，a的φ(n)次方减去1，可以被n整除。  
比如，3和7互质，而7的欧拉函数φ(7)等于6，所以3的6次方（729）减去1，可以被7整除（728/7=104）。

### RSA算法

***逆推***

设{e,n}为公钥，{d,n}为私钥，m为明文，c为密文
假设我们在加密时需要如下公式：
```
m的e次方 mod n = c
```
假设我们在解密时需要如下公式：
```
c的d次方 mod n = m
由于c = m的e次方 mod n，解密可以写为：
m的e次方的d次方 mod n = m 或 m的e*d次方 mod n = m 
```
由于{e,n}是公钥，因此需要让d很难计算出来  
对于m和n两个互质的整数，且m<n，根据欧拉公式：
```
 m^φ(n) mod n = 1
 m^[φ(n)+1] mod n = m (相当于多乘以一个m，m个m^φ(n)；每个m^φ(n) mod n余1，那么m^[φ(n)+1] mod n 余下来的就是 m)
```
至此，可以让e*d = φ(n)+1，d = [φ(n)+1] / e  但是计算出来的d不一定为整数，所以还需要想办法，可以给 φ(n)加个系数k，让k变化直到d为整数
```
 m^φ(n) mod n = 1  [欧拉公式，m和n互为质数]
 m^φ(n)*k mod n = 1  这是因为 m^φ(n) mod n =1 那么 m^φ(n)的k次方mod n 也就为1
```
至此，e*d = kφ(n)+1  d = [k*φ(n)+1] / e , 上面的过程是逆推，下面进行正推  

***正推***

设m和n互为质数,则根据欧拉公式:
```
m^φ(n) mod n = 1
m^[k*φ(n)+1] mod n = m
寻找e和d，使得e*d = [k*φ(n)+1]
则 m^(e*d) mod n = m
m^e mod n  对m进行加密，为密文c
c^d mod n = m 对c进行解密，m为明文 
```

## 安全性

**尝试破解1**  
公钥为{e,n}，私钥为{d,n}，m为明文，c为密文；已知条件：{e,n} m和c 求d，
```
m^e mod n = c
m^(ed) mod n = m  因此可以遍历d，任取m直到等式相等获得d
```
下面是js代码，演示了获取d的过程：
```
// 公钥是{7,187}  m为99 c为176 下面开始算私钥
// 99 的 7次方 mod 187 等于 176
console.log("99 的 7次方 mod 187 等于 "+bigInt(99).modPow(7,187));
for(var i=1;i<100000;i++){
   if(bigInt(3).modPow(7*i,187) == 3){ // m选择3，i其实就是d
		if(bigInt(9).modPow(7*i,187) == 9){  // m选择9，再校验一遍
			console.log("d 等于 "+i+"  176的d次方 mod 187 ：" + bigInt(176).modPow(i,187));
		}
	}
}
//输出如下，可以看到d有很多取值，都可以将密文c197恢复到明文99
//test.html:26 d 等于 23   176的d次方 mod 187 ：99
//test.html:26 d 等于 103  176的d次方 mod 187 ：99
//test.html:26 d 等于 183  176的d次方 mod 187 ：99
//test.html:26 d 等于 263  176的d次方 mod 187 ：99
//test.html:26 d 等于 343  176的d次方 mod 187 ：99
```
java代码如下：
```
// 此处e为65537
for(int i=Integer.MAX_VALUE;i>0;i--){
	BigInteger test = new BigInteger(String.valueOf(65537*i));
	if(three.modPow(test,n).compareTo(three) == 0){
	  System.out.println("d 等于 "+i+"  c的d次方 mod n ：" + c.modPow(new BigInteger(String.valueOf(i)),n));
	}
}
```
分析： m^(ed) mod n = m  因此可以遍历d，任取m直到等式相等获得d；先看一下d =  [k*φ(n)+1] / e , 若n选取一个n-bit的二进制质数，φ(n)的范围在2*n-bit范围内，若n远大于e的位数，则d的范围大约也在n-bit范围内，需要遍历2的n次方以查找d，符合条件的k最多有设n/e个 ，那么符合的d有n/e个，从2的n次方中选择n/e个，由于n足够大时，n/e远小于2的n次方，因此找到的几率非常小。

**尝试破解2**   

由于已经知道了{e,n}，并且d =  [k*φ(n)+1] / e , 可以计算出φ(n)，然后选取k，得到d，以上面的程序为例，
```
公钥是{7,187}  m为99 c为176 
将187进行因式分解，得到11*17，都是质数，因此φ(187) = 10 * 16 = 160
d =  [k*φ(n)+1] / e   当k等于1时，d = 161/7 = 23
176的23次方mod 197 = 99 从176恢复了明文99
```
分析：已知n，计算φ(n)，可以选择对n进行因式分解，通过公式求出；也可以不断便利1～n，计算出互质的数量；设n为二进制为n-bit的数，因式分解的时间复杂度为O(2^n)，遍历也需要2的n次方，根据上面描述可知找到d的几率非常小。

## RSA过程

1. 找到两个非常大的质数p1和p2
2. 计算n = p1 * p2 , φ(n) = (p1-1) * (p1-2)，
3. 选择一个e，e为正整数，与φ(n) 互质，{e,n}即为公钥，
4. 根据d = [k*φ(n)+1] / e 调整k并计算选择d，{d,n}为公钥
5. 加密： m的e次方mod n 为c  m为明文，c为密文
6. 解密： c的d次方mod n 为m  m为明文，c为密文


## java使用

```
//1.初始化密钥
KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
keyPairGenerator.initialize(521);
KeyPair keyPair = keyPairGenerator.generateKeyPair();
RSAPublicKey rsaPublicKey = (RSAPublicKey) keyPair.getPublic();
RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) keyPair.getPrivate();
System.out.println("Public key is " + Base64.getEncoder().encodeToString(rsaPublicKey.getEncoded()));
System.out.println("Private key is " +  Base64.getEncoder().encodeToString(rsaPrivateKey.getEncoded()));
```