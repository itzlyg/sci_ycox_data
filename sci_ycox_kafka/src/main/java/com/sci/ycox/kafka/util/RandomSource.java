package com.sci.ycox.kafka.util;

import java.util.Random;

public class RandomSource {


	private static Random RDINT = new Random();
	
	
	private static String[] APP_IDS = {"APP_A0001","APP_A0002","APP_A0003","APP_A0004","APP_B0001","APP_B0011","APP_A0021","APP_A0041"};
	
	private static String BASECHAR = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ_";
	
	private static String[] URLS = {"home","index","qr_lock","wechar_scol","queryInfo","list","update_info"};

    private static String[] SIS = {"si_home","si_index","si_qr_lock","si_wechar_scol","si_queryInfo","si_list","si_update_info"};
	
	private static String[] VERSIONS = {"1.0.0","1.2.21","2.0.1","3.0","1.22.3"};
	
	private static String[] IDS = {"user_id_a_001", "user_id_a_002", "user_id_a_003", "user_id_a_004", "user_id_b_001", "user_id_b_002", "user_id_b_003", "user_id_b_004", "user_id_b_005", "user_id_b_006"};
	
	private static int[][] IPS = { { 607649792, 608174079 }, // 36.56.0.0-36.63.255.255
			{ 1038614528, 1039007743 }, // 61.232.0.0-61.237.255.255
			{ 1783627776, 1784676351 }, // 106.80.0.0-106.95.255.255
			{ 2035023872, 2035154943 }, // 121.76.0.0-121.77.255.255
			{ 2078801920, 2079064063 }, // 123.232.0.0-123.235.255.255
			{ -1950089216, -1948778497 }, // 139.196.0.0-139.215.255.255
			{ -1425539072, -1425014785 }, // 171.8.0.0-171.15.255.255
			{ -1236271104, -1235419137 }, // 182.80.0.0-182.92.255.255
			{ -770113536, -768606209 }, // 210.25.0.0-210.47.255.255
			{ -569376768, -564133889 }, // 222.16.0.0-222.95.255.255
	};
	
	private static String[] LGS = {"zh-CN","zh-HK","zh-MO","zh-TW","en-US","en-GB","af-ZA","fr-FR","de-DE","ro-RO"};

	/** 性别权重 0-10 12-20……  40-50    50-60 …… 90-100  (76,24)   */
	private static int[] AGE_WEIGHT = {0, 2, 20, 31, 23,  13, 7, 2, 1, 1 };

	/**
	 *  获取随机URL
	 * @return
	 */
	public static String url () {
		StringBuffer sb = new StringBuffer("/api/flink/yocx/");
		int len = URLS.length + 1;
		int index = random(len);
		if (index == URLS.length) {
			index = 0;
			while (index < 5) {
				index = random(15);
			}
			int temp = 0;
			for (int i = 0; i < index; i++) {
				temp = random(BASECHAR.length());
				sb.append(BASECHAR.charAt(temp));
			}
		} else {
			sb.append(URLS[index]);
		}
		return sb.toString();
	}


	
	/**
	 * pvi
	 * @return
	 */
	public static String pvi () {
		int index = random(IDS.length);
		return IDS[index];
	}
	/**
	 * ty
	 * @return
	 */
	public static String ty () {
		String ty = "1";
		int index = random(130);
		if (index < 10) {
			ty = "0";
		}
		return ty;
	}
	
	/**
	 * 语言
	 * @return
	 */
	public static String lg() {
		int index = random(LGS.length);
		return LGS[index];
	}

	/**
	 * 时区
	 * @return
	 */
	public static String tz() {
		int index = random(12);
		int zf = random(100);
		if (zf % 2 != 0) {
			index *= -1;
		}
		return index + "";
	}
	
	/**
	 *  获取随机appId
	 * @return
	 */
	public static String appId () {
		int index = random(APP_IDS.length);
		return APP_IDS[index];
	}
	
	/**
	 * 获取版本号
	 * @return
	 */
	public static String version() {
		int index = random(VERSIONS.length);
		return VERSIONS[index];
	}
	
	/**
	 * 将十进制转换成IP地址
	 * @return
	 */
	public static String ip() {
		int index = random(10);
		int[] b = new int[4];
		int ip = IPS[index][0] + random(IPS[index][1] - IPS[index][0]);
		b[0] = (int) ((ip >> 24) & 0xff);
		b[1] = (int) ((ip >> 16) & 0xff);
		b[2] = (int) ((ip >> 8) & 0xff);
		b[3] = (int) (ip & 0xff);
		return String.format("%d.%d.%d.%d", b[0], b[1], b[2], b[3]);
	}

	/**
	 * 性别
	 * @return
	 */
	public static String sex(){
		int i = random(100);
		// 男
		if(i < 60){
			return "female";
		} else {
			return "male";
		}

	}

    /**
     * SI
     * @return
     */
	public static String si(){
        StringBuffer sb = new StringBuffer("");
        int len = SIS.length + 1;
        int index = random(len);
        if (index == SIS.length) {
            index = 0;
            while (index < 5) {
                index = random(10);
            }
            int temp = 0;
            for (int i = 0; i < index; i++) {
                temp = random(BASECHAR.length());
                sb.append(BASECHAR.charAt(temp));
            }
        } else {
            sb.append(SIS[index]);
        }
        return sb.toString();
    }

	/**
	 * 权重计算年龄
	 * @return
	 */
	public static int age(){
		int w = printNum(AGE_WEIGHT);
		int ran = random(10);
		return  w * 10 + ran;
	}

	/**
	 * 权重计算
	 * @param weiht 权重数组
	 * @return
	 */
	private static synchronized int printNum(int[] weiht) {
		int randomNum = random(100);
		int sum = 0;
		for (int i = 0, j = weiht.length; i < j; i++) {
			sum += weiht[i];
			if (randomNum <= sum) {
				return i;
			}
		}
		return 0;
	}
	
	public static int random (int max) {
		return RDINT.nextInt(max);
	}
}
