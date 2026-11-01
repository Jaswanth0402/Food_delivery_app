package com.jaswanth.fooddeliveryplatform.test;

public class SlidingWindow {

    public static void main(String[] args) {

    }

    public int maxSum(int[] arr, int k){

        int max =0;
        int windowSum =0;
       for(int i =0; i<k; i++){
           windowSum += arr[i];
       }
       max = windowSum;

       for(int i = k; i< arr.length; i++){
//           newSum = oldSum - outgoing + incoming
           windowSum = windowSum - arr[i-k] + arr[i];

           if(windowSum> max){
               max = windowSum;
           }
       }


       return max;
    }


    public int minSubArrayLen(int target, int[] arr) {
//[2, 3, 1, 2, 4, 3]
//        target = 7
        int left = 0;
        int sum = 0;
        int minLength = Integer.MAX_VALUE;

        for (int right = 0; right < arr.length; right++) {

            sum += arr[right];

            while (sum >= target) {

                minLength = Math.min(
                        minLength,
                        right - left + 1
                );

                sum -= arr[left];
                left++;
            }
        }

        return minLength == Integer.MAX_VALUE ? 0 : minLength;
    }


    public int maxSubArrayLen(int[] arr, int target) {
//[2, 1, 5, 1, 3, 2]
//        target = 7
        int left = 0;
        int sum = 0;
        int maxLength = 0;

        for (int right = 0; right < arr.length; right++) {

            sum += arr[right];

            while (sum > target) {
                sum -= arr[left];
                left++;
            }

            maxLength = Math.max(
                    maxLength,
                    right - left + 1
            );
        }

        return maxLength;
    }
}
