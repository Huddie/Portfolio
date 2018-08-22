// Ehud Adler
// 8.22.18
//
// Median
// https://leetcode.com/problems/median-of-two-sorted-arrays
// Runtime: 48ms
//

func findMedianSortedArrays(_ nums1: [Int], _ nums2: [Int]) -> Double {
  
    // Initial check. If we find 1 array empty the median
    // in the median of the other one
    if nums1.isEmpty {
        return nums2.findMedian()
    } else if nums2.isEmpty {
        return nums1.findMedian()
    }
    let merged = nums1.mergeSorted(with: nums2)
    return merged.findMedian()
}

extension Array where Element == Int {
  
  func mergeSorted(with arr: [Int]) -> [Int] {
    
    var runner1 = 0, runner2 = 0
    var merged = [Int]()
    while runner1 < self.count && runner2 < arr.count {
      merged.append(
        self[runner1] < arr[runner2]
          ? self[runner1.postIncrement()]
          : arr[runner2.postIncrement()]
      )
    }
    
    while runner1 < self.count {
      merged.append(self[runner1.postIncrement()])
    }
    
    while runner2 < arr.count {
      merged.append(arr[runner2.postIncrement()])
    }
    
    return merged
  }
  
  func findMedian() -> Double {
    let count = self.count, midPoint = self[(count / 2)]
    return count % 2 == 0
      ? Double((midPoint + self[(count / 2) - 1])) / 2.0
      : Double(midPoint)
  }
}

extension Int {
  mutating func postIncrement() -> Int {
    defer {self = self + 1}
    return self
  }
}
