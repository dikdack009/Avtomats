import random


class RandomLineGenerator:
    # "ed2k://|server|200.0.12.54|24345|/"

    def addUpperOrLower(self, letter):
        isCapital = random.choice([True, False])
        if isCapital:
            return letter.upper()
        else:
            return letter.lower()

    def genAcceptableLine(self):
        headLetters = ["e", "d", "2", "k", ":", "/", "/", "|", "s", "e", "r", "v", "e", "r", "|"]
        result = ""
        for letter in headLetters:
            result += self.addUpperOrLower(letter)
        result += str(random.randint(0, 255)) + "."
        result += str(random.randint(0, 255)) + "."
        result += str(random.randint(0, 255)) + "."
        result += str(random.randint(0, 255)) + "|"
        result += str(random.randint(0, 65535)) + "|" + "/"

        return result

    def genUnacceptableLine(self):
        headLetters = ["e", "d", "2", "k", ":", "/", "/", "|", "s", "e", "r", "v", "e", "r", "|"]

        result = ""
        errInHead1 = "|d2k://|server|"
        errInHead2 = "eB2k://|server|"
        errInHead3 = "ed3k://|server|"
        errInHead4 = "ed25k://|server|"
        errInHead5 = "ed2L://|server|"
        errInHead6 = "ed2k//|server|"
        errInHead7 = "ed2k:|/|server|"
        errInHead8 = "ed2k:/)|server|"
        errInHead9 = "ed2k://||server|"
        errInHead10 = "ed2k://|Eerver|"
        errInHead11 = "ed2k://|srver|"
        errInHead12 = "ed2k://|seNver|"
        errInHead13 = "ed2k://|serGer|"
        errInHead14 = "ed2k://|servEEr|"
        errInHead15 = "ed2k://|serveT|"
        errInHead16 = "ed2k://|serveT||"
        errInHead = random.choice([errInHead1, errInHead2, errInHead3, errInHead4, errInHead5, errInHead6,
                                   errInHead7, errInHead8, errInHead9, errInHead10, errInHead11, errInHead12,
                                   errInHead13, errInHead14, errInHead15, errInHead16])
        errInOct1 = str(random.randint(0, 255)) + "@"
        errInOct2 = str(random.randint(256, 1000))
        errInOct3 = "@&*#$^"
        errInOctet = random.choice([errInOct1, errInOct2, errInOct3])

        errInPort1 = str(random.randint(0, 65535)) + "@"
        errInPort2 = str(random.randint(65535, 920357792305))
        errInPort3 = "@&*#$^"
        errInPort = random.choice([errInPort1, errInPort2, errInPort3])

        typeOfUnacceptableLine = random.randint(0, 12)
        if typeOfUnacceptableLine == 0:
            result += errInHead
            result += str(random.randint(0, 255)) + "." + str(random.randint(0, 255)) + "." + \
                      str(random.randint(0, 255)) + "." + str(random.randint(0, 255)) + "|" + \
                      str(random.randint(0, 65535)) + "|/"

        elif typeOfUnacceptableLine == 1:
            for letter in headLetters:
                result += self.addUpperOrLower(letter)
            result += errInOctet + "."
            result += str(random.randint(0, 255)) + "." + str(random.randint(0, 255)) + "." + \
                      str(random.randint(0, 255)) + "|" + str(random.randint(0, 65535)) + "|/"

        elif typeOfUnacceptableLine == 2:
            for letter in headLetters:
                result += self.addUpperOrLower(letter)
            result += str(random.randint(0, 255)) + "." + errInOctet + "." + str(random.randint(0, 255)) +\
                      "." + str(random.randint(0, 255)) + "|" + str(random.randint(0, 65535)) + "|/"
        elif typeOfUnacceptableLine == 3:
            for letter in headLetters:
                result += self.addUpperOrLower(letter)
            result += str(random.randint(0, 255)) + "." + str(random.randint(0, 255)) + "." +\
                      errInOctet + "." + str(random.randint(0, 255)) + "|" +\
                      str(random.randint(0, 65535)) + "|/"
        elif typeOfUnacceptableLine == 4:
            for letter in headLetters:
                result += self.addUpperOrLower(letter)
            result += str(random.randint(0, 255)) + "." + str(random.randint(0, 255)) + "." +\
                      str(random.randint(0, 255)) + "." + errInOctet + "|" +\
                      str(random.randint(0, 65535)) + "|/"
        elif typeOfUnacceptableLine == 5:
            for letter in headLetters:
                result += self.addUpperOrLower(letter)

            result += str(random.randint(0, 255)) + "@" + str(random.randint(0, 255)) + "." +\
                      str(random.randint(0, 255)) + "." + str(random.randint(0, 255)) + "|" +\
                      str(random.randint(0, 65535)) + "|/"
        elif typeOfUnacceptableLine == 6:
            for letter in headLetters:
                result += self.addUpperOrLower(letter)
            result += str(random.randint(0, 255)) + "." + str(random.randint(0, 255)) + "@" + \
                      str(random.randint(0, 255)) + "." + str(random.randint(0, 255)) + "|" + \
                      str(random.randint(0, 65535)) + "|/"
        elif typeOfUnacceptableLine == 7:
            for letter in headLetters:
                result += self.addUpperOrLower(letter)
            result += str(random.randint(0, 255)) + "." + str(random.randint(0, 255)) + "." +\
                      str(random.randint(0, 255)) + "@" + str(random.randint(0, 255)) + "|" +\
                      str(random.randint(0, 65535)) + "|/"
        elif typeOfUnacceptableLine == 8:
            for letter in headLetters:
                result += self.addUpperOrLower(letter)
            result += str(random.randint(0, 255)) + "." + str(random.randint(0, 255)) + "." +\
                      str(random.randint(0, 255)) + "." + str(random.randint(0, 255)) + "|" +\
                      errInPort + "|/"
        elif typeOfUnacceptableLine == 9:
            for letter in headLetters:
                result += self.addUpperOrLower(letter)
            result += str(random.randint(0, 255)) + "." + str(random.randint(0, 255)) + "." +\
                      str(random.randint(0, 255)) + "." + str(random.randint(0, 255)) + "|" +\
                      str(random.randint(0, 65535)) + "@|/"
        elif typeOfUnacceptableLine == 10:
            for letter in headLetters:
                result += self.addUpperOrLower(letter)
            result += str(random.randint(0, 255)) + "." + str(random.randint(0, 255)) + "." +\
                      str(random.randint(0, 255)) + "." + str(random.randint(0, 255)) + "|" +\
                      str(random.randint(0, 65535)) + "|@/"
        elif typeOfUnacceptableLine == 11:
            for letter in headLetters:
                result += self.addUpperOrLower(letter)
            result += str(random.randint(0, 255)) + "." + str(random.randint(0, 255)) + "." +\
                      str(random.randint(0, 255)) + "." + str(random.randint(0, 255)) + "|" +\
                      str(random.randint(0, 65535)) + "|/@"
        elif typeOfUnacceptableLine == 12:
            for letter in headLetters:
                result += self.addUpperOrLower(letter)
            result += str(random.randint(0, 255)) + "." + str(random.randint(0, 255)) + "." +\
                      str(random.randint(0, 255)) + "." + str(random.randint(0, 255)) + "@" +\
                      str(random.randint(0, 65535)) + "|/"
        return result