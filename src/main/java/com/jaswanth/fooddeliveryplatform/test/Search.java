package com.jaswanth.fooddeliveryplatform.test;



public class Search {

    public static void main(String[] args) {

        Search linear = new Search();
        int[] arr =  {12, 45, 7, 89, 23, 56};
        linear.linearSearch(arr,91);

    }

    // Linear Search
    public int linearSearch(int[] arr, int target){

        for(int i=0;i< arr.length;i++){
            if(arr[i] == target){
                return i;
            }
        }
        return -1;
    }

//   1. What happens when target = 89?
//    return index for the target 89 = 3;

//    2.What happens when target = 100?
//    return index for the target 100 = in that array the value 100 is not there so return -1;

//    3  Best-case Time Complexity = O(1);
//    4.Worst-case Time Complexity = O(n);
//    5.Space Complexity = O(1)

    public int binerySearch(int[] arr, int target){
       int left = 0;
       int right = arr.length-1;


        while (left <= right){
            int mid = (left+right)/2;

            if(arr[mid] == target){
                return  mid;
            } else if (arr[mid]> target) {
                right = mid -1;

            }
            else {
                left = mid +1;
            }


        }
        return -1;
    }
}
