fn main() {
    // Declare a variable
    let mut num = 5;

    // Print the current value
    println!("Original value of num: {}", num);
    // Modify the value
    num = add_one(num);

    // Print the updated value
    println!("Updated value of num: {}", num);
}

fn add_one(x: i32) -> i32 {
    x + 1
}