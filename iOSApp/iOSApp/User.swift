import Foundation

class User: NSObject, NSCoding{
	/**
	id of the user
	*/
    let id: Int!
	
	/**
	token of the user
	*/
    var token: String!
    
    init(id: Int, token: String) {
        self.id = id
        self.token = token
    }
    
    required init(coder aDecoder: NSCoder) {
        id = aDecoder.decodeObject(forKey: "id") as! Int
        token = aDecoder.decodeObject(forKey: "token") as! String
    }

    
    func encode(with aCoder: NSCoder) {
        aCoder.encode(id, forKey: "id")
        aCoder.encode(token, forKey: "token")
    }
}
