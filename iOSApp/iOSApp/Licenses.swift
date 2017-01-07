import Foundation

class Licenses: NSObject{
	
	private(set) var name: String!
	private(set) var url: String!
	private(set) var copyright: String!
	private(set) var license: String!
	
	init(name: String, url: String, copyright: String, license: LicensesEnum) {
		self.name = name
		self.url = url
		self.copyright = copyright
		
		let path: String
		switch license {
		case .apache20:
			path = Bundle(for: Licenses.self).path(forResource: "Apache20", ofType: "txt")!
			break
		case .obdl10:
			path = Bundle(for: Licenses.self).path(forResource: "ODbL10", ofType: "txt")!
			break
		default:
			break
		}
		
		do {
			self.license = try String(contentsOfFile: path, encoding: .utf8)
		}catch{
			
		}
		
	}
}
