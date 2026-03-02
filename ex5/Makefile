BASEDIR           = $(shell pwd)
JFlex_DIR         = ${BASEDIR}/jflex
CUP_DIR           = ${BASEDIR}/cup
SRC_DIR           = ${BASEDIR}/src
BIN_DIR           = ${BASEDIR}/bin
EXTERNAL_JARS_DIR = ${BASEDIR}/external_jars
MANIFEST_DIR      = ${BASEDIR}/manifest

JFlex_GENERATED_FILE      = ${SRC_DIR}/Lexer.java
CUP_GENERATED_FILES       = ${SRC_DIR}/Parser.java ${SRC_DIR}/TokenNames.java
JFlex_CUP_GENERATED_FILES = ${JFlex_GENERATED_FILE} ${CUP_GENERATED_FILES}
SRC_FILES                 = ${SRC_DIR}/*.java ${SRC_DIR}/*/*.java
EXTERNAL_JAR_FILES        = ${EXTERNAL_JARS_DIR}/java-cup-11b-runtime.jar
MANIFEST_FILE             = ${MANIFEST_DIR}/MANIFEST.MF

JFlex_PROGRAM  = jflex
JFlex_FLAGS    = -q
JFlex_DEST_DIR = ${SRC_DIR}
JFlex_FILE     = ${JFlex_DIR}/LEX_FILE.lex

CUP_PROGRAM                    = java -jar ${EXTERNAL_JARS_DIR}/java-cup-11b.jar
CUP_FILE                       = ${CUP_DIR}/CUP_FILE.cup
CUP_GENERATED_PARSER_NAME      = Parser
CUP_GENERATED_SYMBOLS_FILENAME = TokenNames

CUP_FLAGS = \
-nowarn \
-parser  ${CUP_GENERATED_PARSER_NAME} \
-symbols ${CUP_GENERATED_SYMBOLS_FILENAME}

all: compile

compile:
	@rm -rf COMPILER
	@rm -rf ${JFlex_CUP_GENERATED_FILES} ${BIN_DIR}
	@$(JFlex_PROGRAM) ${JFlex_FLAGS} -d ${JFlex_DEST_DIR} ${JFlex_FILE}
	@$(CUP_PROGRAM) ${CUP_FLAGS} -destdir ${SRC_DIR} ${CUP_FILE}
	@mkdir -p ${BIN_DIR}
	@javac -cp ${EXTERNAL_JAR_FILES} -d ${BIN_DIR} ${SRC_FILES}
	@jar cfm COMPILER ${MANIFEST_FILE} -C ${BIN_DIR} .

clean:
	rm -rf COMPILER ${BIN_DIR} ${JFlex_CUP_GENERATED_FILES}
