package com.jaswanth.fooddeliveryplatform.test;

class MinMax {
    int min;
    int max;

    MinMax(int min, int max) {
        this.min = min;
        this.max = max;
    }
}

public class Test {

    public int findMin(int[] n){

        int min =n[0];

        for(int i =0; i< n.length; i++){
            if(n[i]<min){
                min = n[i];
            }
        }

        return min;
    }

    public int secondLargest(int[] n){

        int max =n[0];
        int secondMax =n[0];

        for(int i =0; i< n.length; i++){
            if(n[i]>max){
                secondMax = max;
                max = n[i];
            }
            else if (n[i] > secondMax && n[i] < max) {
                secondMax = n[i];
            }
        }

        return secondMax;
    }
    public int findSum(int[] n){

        int sum =0;


        for(int i =0; i< n.length; i++){
            sum += n[i];
        }

        return sum;
    }
    public int countEven(int[] n){

        int count =0;


        for(int i =0; i< n.length; i++){
            if(n[i]%2 == 0){
                count ++;
            }

        }

        return count;
    }


    public MinMax findMinAndMax(int[] n){

        int min =n[0];
        int max =  n[0];

        for(int i =0; i< n.length; i++){
            if(n[i]<min){
                min = n[i];
            }
            if(n[i]>max){
                max = n[i];
            }
        }

        return new MinMax(min,max);
    }

    public static void main(String[] args) {

        Test min = new Test();

        int[] minimum = {34, 12, 78, 5, 91, 43, 67};
        int[] secLarge = {34, 12, 78, 5, 91, 43, 67};

        int minimumValue = min.findMin(minimum);
        int secondLarge = min.findMin(secLarge);


    }
}





