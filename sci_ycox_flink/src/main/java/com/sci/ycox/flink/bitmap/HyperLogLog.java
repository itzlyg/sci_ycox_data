package com.sci.ycox.flink.bitmap;

public class HyperLogLog {
    /** 64位hash值中标记分组索引的bit数量，分组越多误差越小，但占用的空间越大 */
    private static final int HLL_P = 14;
    /** 总的分组数量 */
    private static final int HLL_REGISTERS = 1 << HLL_P;
    /** 为保存每一个分组中最大起始0统计量，所需要的bit数量 */
    private static final int HLL_BITS = 6;
    /** 统计量的6位掩码 */
    private static final int HLL_REGISTER_MASK = (1 << HLL_BITS) - 1;
    /**
     * bitmap存储格式，采用小端存储，先存储最低有效位，然后存储最高有效位
     * +--------+--------+--------+------//      //--+
     * |11000000|22221111|33333322|55444444 ....     |
     * +--------+--------+--------+------//      //--+
     */
    private byte[] registers;

    public HyperLogLog() {
        //12288+1(12k)个字节，最后一个额外的字节相当于结束符，并没有实际用途
        registers = new byte[(HLL_REGISTERS * HLL_BITS + 7) / 8 + 1];
    }

    //alpha系数,来自参考论文
    private double alpha(int m) {
        switch (m) {
            case 16:
                return 0.673;
            case 32:
                return 0.697;
            case 64:
                return 0.709;
            default:
                return 0.7213 / (1 + 1.079 / m);
        }
    }

    //保存第index分组的值为val
    private void setRegister(int index, int val) {
        int _byte = index * HLL_BITS / 8;
        int _fb = index * HLL_BITS & 7;
        int _fb8 = 8 - _fb;
        registers[_byte] &= ~(HLL_REGISTER_MASK << _fb);
        registers[_byte] |= val << _fb;
        registers[_byte + 1] &= ~(HLL_REGISTER_MASK >> _fb8);
        registers[_byte + 1] |= val >> _fb8;
    }
    //读取第index分组的值
    private int getRegister(int index) {
        int _byte = index * HLL_BITS / 8;
        int _fb = index * HLL_BITS & 7;
        int _fb8 = 8 - _fb;
        int b0 = registers[_byte] & 0xff;
        int b1 = registers[_byte + 1] & 0xff;
        return ((b0 >> _fb) | (b1 << _fb8)) & HLL_REGISTER_MASK;
    }

    public int hllAdd(int number) {
        long hash = MurmurHash.hash64(Integer.toString(number).getBytes());
        long index = hash >>> (64 - HLL_P);
        int oldcount = getRegister((int) index);

        //计算hash值中从HLL_P为开始的连续0数量，包括最后一个1
        hash |= 1L;
        long bit = 1L << (63 - HLL_P);
        int count = 1;
        while ((hash & bit) == 0L) {
            count++;
            bit >>= 1L;
        }

        if (count > oldcount) {
            setRegister((int) index, count);
            return 1;
        } else {
            return 0;
        }
    }

    //估算基数
    public long hllCount() {
        //计算各分组统计量的调和平均数，SUM(2^-reg)
        double E = 0;
        int ez = 0;
        double m = HLL_REGISTERS;
        for (int i = 0; i < HLL_REGISTERS; i++) {
            int reg = getRegister(i);
            if (reg == 0) {
                ez++;
            } else {
                E += 1.0d / (1L << reg);
            }
        }
        E += ez;

        E = 1 / E * alpha((int) m) * m * m;

        if (E < m * 2.5 && ez != 0) {
            E = m * Math.log(m / ez);
        } else if (m == 16384 && E < 72000) {
            //来自redis源码
            double bias = 5.9119e-18 * E * E * E * E
                    - 1.4253e-12 * E * E * E
                    + 1.2940e-7 * E * E
                    - 5.2921e-3 * E
                    + 83.3216;
            E -= E * (bias / 100);
        }
        return (long) E;
    }
}
