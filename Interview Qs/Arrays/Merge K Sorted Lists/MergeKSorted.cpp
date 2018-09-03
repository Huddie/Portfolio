// Ehud Adler
// 9.2.18
//
// MergekSorted
// https://leetcode.com/problems/merge-k-sorted-lists
// Runtime: 224ms
//


class Solution {
    
public:
    ListNode* merge2Lists(ListNode* list_a, ListNode* list_b) {
 
        // Setup so that list_a has the smaller first element
        if (list_a->val > list_b->val) {
            ListNode* temp = list_a;
            list_a = list_b;
            list_b = temp;
        }

        ListNode* head = list_a;

        while (list_a != nullptr) {
            if (list_b == nullptr
            || (list_a->next != nullptr
            && list_a->next->val < list_b->val)) {
                list_a = list_a->next;
            }
            else  {
                ListNode* temp_a = list_a->next;
                ListNode* temp_b = list_b->next;
                list_a->next = list_b;
                list_a = list_a->next;
                list_b = temp_b;
                list_a->next = temp_a;
            }
        }
        
        return head;
    }
    
    ListNode* mergeKLists(vector<ListNode*>& lists) {
        
        // Initial check
        if (lists.size() == 0) return nullptr;
            
        ListNode *head = nullptr;
        for (unsigned int i = 0; i < lists.size(); i += 1) {
            if ( head == nullptr ) {
                head = lists[i];
                continue;
            }
            head = lists[i] != nullptr ? merge2Lists(head, lists[i]) : head;
        }
        return head;
    }
};