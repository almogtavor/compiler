#!/bin/bash
# Run only INVALID lexer tests
LEXER="./LEXER"
BASE_DIR="edge_cases/invalid"
OUT_DIR="./out_invalid"
mkdir -p "$OUT_DIR"

# Colors
GREEN="\033[1;32m"
RED="\033[1;31m"
YELLOW="\033[1;33m"
CYAN="\033[1;36m"
RESET="\033[0m"

pass=0
fail=0

echo -e "${CYAN}=== Running INVALID tests ===${RESET}"
echo

for f in $BASE_DIR/*.txt; do
    [ -f "$f" ] || continue
    name=$(basename "$f")
    out_file="$OUT_DIR/$name.out"

    java -jar "$LEXER" "$f" "$out_file" >/dev/null 2>&1

    if [ ! -s "$out_file" ]; then
        echo -e "${RED}FAIL${RESET} - $name ${YELLOW}(no output file)${RESET}"
        ((fail++))
    elif grep -q "ERROR" "$out_file"; then
        echo -e "${GREEN}PASS${RESET} - $name"
        ((pass++))
    else
        echo -e "${RED}FAIL${RESET} - $name ${YELLOW}(no ERROR found)${RESET}"
        ((fail++))
    fi
done

echo
echo -e "${CYAN}=== Summary ===${RESET}"
echo -e "  ${GREEN}Passed:${RESET} $pass"
echo -e "  ${RED}Failed:${RESET} $fail"
echo
