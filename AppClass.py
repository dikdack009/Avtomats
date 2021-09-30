#
# The contents of this file are subject to the Mozilla Public
# License Version 1.1 (the "License"); you may not use this file
# except in compliance with the License. You may obtain a copy of
# the License at http://www.mozilla.org/MPL/
#
# Software distributed under the License is distributed on an "AS
# IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
# implied. See the License for the specific language governing
# rights and limitations under the License.
#
# The Original Code is State Machine Compiler (SMC).
#
# The Initial Developer of the Original Code is Charles W. Rapp.
# Portions created by Charles W. Rapp are
# Copyright (C) 2000 - 2005 Charles W. Rapp.
# All Rights Reserved.
#
# Contributor(s):
#       Port to Python by Francois Perrad, francois.perrad@gadz.org
#
# Function
#   Main
#
# Description
#  This routine starts the finite state machine running.
#
# RCS ID
# $Id$
#
# CHANGE LOG
# $Log$
# Revision 1.3  2009/04/19 14:39:47  cwrapp
# Added call to enterStartState before issuing first FSM transition.
#
# Revision 1.2  2005/06/03 19:58:28  cwrapp
# Further updates for release 4.0.0
#
# Revision 1.1  2005/05/28 17:48:29  cwrapp
# Added Python examples 1 - 4 and 7.
#
#

import AppClass_sm
import string

class AppClass:

	def __init__(self):
		self.currentSymbol = ""
		self.currentOkt = ""
		self.currentPort = ""
		self._fsm = AppClass_sm.AppClass_sm(self)
		self._is_acceptable = False

		# Uncomment to see debug output.
		# self._fsm.setDebugFlag(True)

	def CheckString(self, string):

		title = "ed2k://"
		server = "|server|"
		digits19 = {"1", "2", "3", "4", "5", "6", "7", "8", "9"}

		try:
			stringTitle = string[:7]
		except Exception:
			print("FUCK1")
		try:
			stringServer = string[7:15]
		except Exception:
			print("fuck2")

		if str(stringTitle).lower() == title and str(stringServer).lower() == server:
			stringCopy = string[15:]
			self._fsm.enterStartState()
			self._fsm.StringTitle()
			self._fsm.Stick()
			self._fsm.Server()
			self._fsm.Stick()
			for c in stringCopy:
				self.currentSymbol = c
				if c == "/":
					self._fsm.Slash()
				elif c == "|":
					self._fsm.Stick()
				elif c == "0":
					self._fsm.Zero()
				elif c == ".":
					self._fsm.Dot()
				elif c in digits19:
					self._fsm.Digit19()
				else:
					self._fsm.Unknown()
			self._fsm.EOS()
		return self._is_acceptable

	def Acceptable(self):
		self._is_acceptable = True

	def Unacceptable(self):
		self._is_acceptable = False

	def isValid(self):
		if 0 <= int(self.currentOkt) <= 255:
			return True
		else:
			return False

	def MakeCurrentOktNull(self):
		self.currentOkt = ""

	def MakeCurrentOkt(self):
		self.currentOkt += self.currentSymbol

	def MakePort(self):
		self.currentPort += self.currentSymbol

	def isPortValid(self):
		if 1 <= int(self.currentPort) <= 65535:
			return True
		else:
			return False
