// Ehud Adler
// 8.23.18
//
// Median
// https://leetcode.com/problems/median-of-two-sorted-arrays
// Runtime: 16ms
//

class Solution {
    
vector<int> merge(const vector<int>&,const  vector<int>&);
double median(const vector<int>&);

public:
    double findMedianSortedArrays(
        vector<int>& nums1,
        vector<int>& nums2) {
            
        // Initial Check
        if (nums1.empty()) {
               return get_median(nums2);
        } else if (nums2.empty()) {
               return get_median(nums1);
        }
        vector<int> merged = merge_sorted(nums1, nums2);
        return get_median(merged);
    }

    vector<int> merge_sorted(
        const vector<int>& arr1,
        const vector<int>& arr2) {
        
        unsigned int runner1, runner2;
        runner1 = runner2 = 0;
        vector<int> merged;

        while (runner1 < arr1.size() && runner2 < arr2.size()) {
            merged.push_back(
                arr1[runner1] < arr2[runner2] 
                ? arr1[runner1++] 
                : arr2[runner2++]
            );
        }

        while (runner1 < arr1.size()) {
            merged.push_back(arr1[runner1++]);
        }

        while (runner2 < arr2.size()) {
            merged.push_back(arr2[runner2++]);
        }
        return merged;
    }

    double get_median(const vector<int>& arr) {
        unsigned int count = arr.size(),
        midPoint = count / 2;

        return count % 2 == 0 
        ? (arr[midPoint] +  arr[midPoint-1]) / 2.0
        : (double)arr[midPoint];
    }
};
