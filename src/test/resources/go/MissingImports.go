package main

type MissingImports struct{}

func (self MissingImports) printRandomNumber() {
	fmt.Println(rand.Intn(100))
}

func (self MissingImports) printCurrentDate() {
	fmt.Println(time.Now().Format("2006-01-02"))
}

func (self MissingImports) printFileExists() {
	_, err := os.Stat("example.txt")
	if err == nil {
		fmt.Println(true)
	} else if os.IsNotExist(err) {
		fmt.Println(false)
	} else {
		fmt.Println("Error checking file:", err)
	}
}

func (self MissingImports) printLocalDate() {
	fmt.Println(time.Now().Format("2006-01-02"))
}

func (self MissingImports) printZoneId() {
	fmt.Println(time.Now().Location())
}
