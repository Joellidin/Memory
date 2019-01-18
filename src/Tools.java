// Joel Lidin and Filip Ahlman, Group 39

public class Tools {
    public static void randomOrder(Object[] arr) {
        Object[] tmp = new Object[arr.length];
        int rand, i = 0;
        while(i<arr.length) {
            rand = (int)(Math.random()*arr.length);
            if (tmp[rand] == null) {
                tmp[rand] = arr[i++];
            }
        }
        for (i = 0; i<arr.length; i++) {
            arr[i] = tmp[i];
        }
    }
}
