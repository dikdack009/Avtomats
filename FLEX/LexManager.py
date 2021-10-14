import ply.lex as lex


class LexManager:
    def __init__(self):
        self.port = ""
        self.lexer = lex.lex(self)

    tokens = (
        "HEAD",
        "DIVIDER",
        "SERVER",
        "DOT",
        "NUM",
        "SLASH"
    )

    avaliableTokens = []

    def t_SLASH(self, t):
        r"/"
        return t

    def t_HEAD(self, t):
        r"[eE][dD]2[kK]://"
        return t

    def t_DIVIDER(self, t):
        r"\|"
        return t

    def t_SERVER(self, t):
        r"[sS][eE][rR][vV][eE][rR]"
        return t

    def t_NUM(self, t):
        r"\d+"
        return t

    def t_DOT(self, t):
        r"\."
        return t

    def t_error(self, t):
        return False

    def highlightAvaliableTokens(self, line):
        self.avaliableTokens.clear()
        lineNoSpaces = line.strip()
        self.lexer.input(lineNoSpaces)
        while True:
            try:
                susToken = self.lexer.token()
            except lex.LexError:
                self.avaliableTokens.clear()
                return False
            if not susToken:
                break
            self.avaliableTokens.append(susToken)
        return self.avaliableTokens

    def CheckString(self, line):
        self.highlightAvaliableTokens(line)
        tokensAmount = len(self.avaliableTokens)
        if tokensAmount < 14:
            return False
        currentToken = self.avaliableTokens.pop(0)
        if currentToken.type != "HEAD":
            return False
        currentToken = self.avaliableTokens.pop(0)
        if currentToken.type != "DIVIDER":
            return False
        currentToken = self.avaliableTokens.pop(0)
        if currentToken.type != "SERVER":
            return False
        currentToken = self.avaliableTokens.pop(0)
        if currentToken.type != "DIVIDER":
            return False
        for i in range(3):
            currentToken = self.avaliableTokens.pop(0)
            if currentToken.type != "NUM":
                return False
            if not (255 >= int(currentToken.value) >= 0):
                return False
            currentToken = self.avaliableTokens.pop(0)
            if currentToken.type != "DOT":
                return False
        currentToken = self.avaliableTokens.pop(0)
        if currentToken.type != "NUM":
            return False
        if not (255 >= int(currentToken.value) >= 0):
            return False
        currentToken = self.avaliableTokens.pop(0)
        if currentToken.type != "DIVIDER":
            return False
        currentToken = self.avaliableTokens.pop(0)
        if currentToken.type != "NUM":
            return False
        if not (65535 >= int(currentToken.value) >= 0):
            return False
        self.port = currentToken.value
        currentToken = self.avaliableTokens.pop(0)
        if currentToken.type != "DIVIDER":
            return False
        currentToken = self.avaliableTokens.pop(0)
        if currentToken.type != "SLASH":
            return False
        return True

    def testLine(self, line, stat):
        guess = self.CheckString(line)
        if not guess:
            print(f"\"{line}\" - not acceptable")
        else:
            print(f"\"{line}\" - acceptable")
            stat.orgStatistics(self.port)


