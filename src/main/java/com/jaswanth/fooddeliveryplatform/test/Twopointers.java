package com.jaswanth.fooddeliveryplatform.test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class Pair {
    int first;
    int last;
    boolean find;

    Pair(int first, int last,boolean find) {
        this.first = first;
        this.last = last;
        this.find = find;
    }

   @Override
    public String toString() {
        return "Pair{" +
                "first=" + first +
                ", last=" + last +
                ", find=" + find +
                '}';
    }
}

public class Twopointers {

    public static void main(String[] args) {
        Twopointers t = new Twopointers();
        int[] arr = {2,8,6,4};
        List<Pair> find =  t.findPair2( arr,10);

        System.out.println(find);
    }

    public List<Pair> findPair(int[] arr, int target){
        int left = 0;
        int right = arr.length -1;
      List<Pair> pair = new ArrayList<>();

        while(left<=right){

            if((arr[left] + arr[right]) == target){
//                return new Pair(arr[left],arr[right],true)
                pair.add(new Pair(arr[left],arr[right],true));
                left++;
                right--;
            } else if ((arr[left] + arr[right]) > target) {

                right--;
            }else {
                left++;
            }
        }
        return pair;
    }

    public List<Pair> findPair1(int[] arr, int target){
//        arr = [2, 4, 6, 8]
        List<Pair> pair = new ArrayList<>();
        for(int i = 0; i< arr.length; i++ ) {
            int find = target - arr[i];

            for (int j = i+1; j < arr.length; j++) {

                if (find == arr[j]) {
                     pair.add(new Pair(arr[i],arr[j],true));
                }

            }
        }

        return pair;
    }

    public List<Pair> findPair2(int[] arr, int target) {

        Set<Integer> seen = new HashSet<>();
        List<Pair> pairs = new ArrayList<>();

        for (int num : arr) {

            int complement = target - num;

            if (seen.contains(complement)) {
                pairs.add(new Pair(complement, num, true));
            }

            seen.add(num);
        }

        return pairs;
    }

    public int[] reverseArray(int[] arr){
        int left = 0;
        int right = arr.length -1;
        int[] revarr = new int[arr.length];
        while (left<=right){
            revarr[left] = arr[right];
            revarr[right] = arr[left];
            left++;
            right--;
        }
        return revarr;
    }

    public int[] altreverseArray(int[] arr){
        int left = 0;
        int right = arr.length -1;

        while (left<right){
            int temp = arr[left];
            arr[left] = arr[right];
            arr[right] = temp;
            left++;
            right--;
        }
        return arr;
    }

    public boolean isPalindrome(String s) {

        int left = 0;
        int right = s.length() - 1;

        while (left < right) {

              if(s.charAt(left) != s.charAt(right)){
                  return false;
              }

              left++;
              right--;

        }

        return true;
    }

}
