#!/bin/bash
# Run only VALID lexer tests
LEXER="./LEXER"
BASE_DIR="edge_cases/valid"
OUT_DIR="./out_valid"
mkdir -p "$OUT_DIR"

# Colors
GREEN="\033[1;32m"
RED="\033[1;31m"
YELLOW="\033[1;33m"
CYAN="\033[1;36m"
RESET="\033[0m"

pass=0
fail=0

echo -e "${CYAN}=== Running VALID tests ===${RESET}"
echo

for f in $BASE_DIR/*.txt; do
    [ -f "$f" ] || continue
    name=$(basename "$f")
    out_file="$OUT_DIR/$name.out"

    java -jar "$LEXER" "$f" "$out_file" >/dev/null 2>&1

    if [ ! -s "$out_file" ]; then
        echo -e "${GREEN}PASS${RESET} - $name ${YELLOW}(empty output allowed)${RESET}"
        ((pass++))
    elif grep -q "ERROR" "$out_file"; then
        echo -e "${RED}FAIL${RESET} - $name ${YELLOW}(unexpected ERROR)${RESET}"
        ((fail++))
    else
        echo -e "${GREEN}PASS${RESET} - $name"
        ((pass++))
    fi
done

echo
echo -e "${CYAN}=== Summary ===${RESET}"
echo -e "  ${GREEN}Passed:${RESET} $pass"
echo -e "  ${RED}Failed:${RESET} $fail"
echo
