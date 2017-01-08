import Foundation

enum ErrorTypes: String {
	case connectionFail	= "Error: Connection failed" //Connection to the Database not the server
	
	case tooManyReq		= "Error: Please wait" //After that is the time to wait
	
	case userIdMissing	= "Error: ?u is not set"
	case tokenMissing	= "Error: ?t is not set"
	case eventIdMissing	= "Error: ?k is not set"
	
	case unkownId		= "Error: Unkown ID"
	case invalidToken	= "Error: Invalid Token"
	
	case alreadyPart	= "Error: User is already participating"
	case maxPartReached	= "Error: max participants already reached" //Maximum count of the participants reached
	
}

extension String {
	func contains(error: ErrorTypes) -> Bool{
		return self.contains(error.rawValue)
	}
}
