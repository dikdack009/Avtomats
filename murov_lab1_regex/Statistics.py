class Statistics:
    def __init__(self):
        self.goodPorts = {}
        self.ports = set()
    def orgStatistics(self, port):
        self.ports.add(port)
        # print(port)
        try:
            value = self.goodPorts[port]
            newValue = value + 1
            self.goodPorts[port] = newValue
        except KeyError:
            self.goodPorts[port] = 1
        except Exception:
            print("WTF")

    def printStat(self):
        for port in self.ports:
            print(f"{port} - {self.goodPorts[port]}")

    def clear(self):
        self.goodPorts = {}
        self.ports = set()