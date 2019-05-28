"""
This call sends a message to the given recipient with vars and custom vars.
"""
from mailjet_rest import Client
import os
import sys
api_key = '8136d0e826e2568e77f98e4925d5c874'
api_secret = '449288514d46585ed88c100f0bba4636'
mailjet = Client(auth=(api_key, api_secret), version='v3.1')
listeDoc = ["CNI","RIB","RIB (au nom du mineur)","Acte de décès",
 "Acte de naissance", "Acte de notoriété",
  "Attestation de non résidence ","Copie du jugement de mise sous tutelle"]
data = {
  'Messages': [
		{
			"From": {
				"Email": "pocblockchainna@gmail.com",
				"Name": "POC Blockchain NA"
			},
			"To": [
				{
					"Email": str(sys.argv[1]),
					"Name": "passenger 1"
				}
			],
			"TemplateID": 520562,
			"TemplateLanguage": True,
			"Subject": "Notification réception de pièce",
			"Variables": {
    "LibelleDoc": str(listeDoc[int(sys.argv[2])]),
    "ClientID": str(sys.argv[3]),
    "Receiver": str(sys.argv[4]),
    "DateReception": str(sys.argv[5])
  }
		}
	]
}
result = mailjet.send.create(data=data)

