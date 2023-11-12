# This code has an error, see if Voqal can help you find and fix it.

def binary_search(sorted_list, target):
    low = 0
    high = len(sorted_list) - 1
    while low < high:
        mid = low + (high - low) // 2
        if sorted_list[mid] == target:
            return mid
        elif sorted_list[mid] < target:
            low = mid + 1
        else:
            high = mid
    return -1

def main():
    sorted_list = [1, 3, 5, 7, 9]
    target = 9
    result = binary_search(sorted_list, target)
    if result == -1:
        raise Exception(f"Failed to find {target}.")

main()
