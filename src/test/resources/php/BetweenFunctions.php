<?php
class Test {
    function divide($a, $b) {
        if ($b != 0) {
            return $a / $b;
        } else {
            return "Error: Division by zero";
        }
    }
    function factorial($n) {
        if($n <= 1) {
            return 1;
        }
        return $n * $this->factorial($n - 1);
    }
}