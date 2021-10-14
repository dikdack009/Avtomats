import re
import Statistics

class RegExHandler:
    def __init__(self):
        self.currentPort = ""
    def CheckString(self, line):
        processedLine = line.strip()
        regex = re.compile(r"[eE][dD]2[kK]://"
                           r"\|"
                           r"[sS][eE][rR][vV][eE][rR]"
                           r"\|"
                           r"((?:[0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])"
                           r"\.){3}"
                           r"(?:[0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])"
                           r"\|"
                           r"(?P<Port>(?:[0-9]|[1-9][0-9]|[1-9][0-9][0-9]|[1-9][0-9][0-9][0-9]|[1-5][0-9][0-9][0-9][0-9]|6[1-4][0-9][0-9][0-9]|"
                           r"65[0-4][0-9][0-9]|655[0-2][0-9]|6553[0-5]))"
                           r"\|/$")
        m = re.match(regex, processedLine)
        if m:
            self.currentPort = str(m.group("Port"))
            return True
        else:
            return False

    def testLine(self, line, stat):
                # regex = RegExHandler()
                guess = self.CheckString(line)
                if not guess:
                    print(f"\"{line}\" - not acceptable")
                else:
                    print(f"\"{line}\" - acceptable")
                    stat.orgStatistics(self.currentPort)
