String m_sDisplayText = new String();
Label m_pLabel = new Label();
int m_iPrintedCharacters = 0;

final int k_iTimeToPrint = 5;
int m_iTimer = 0;

PrinterLabel(int i_iLengthCharacters)
{
	m_iLengthCharacters = i_iLengthCharacters;
}

void setText(String text)
{
	m_sDisplayText = text;
	m_iPrintedCharacters = 0;
}

void update(int delta)
{
	if(m_iPrintedCharacters < m_iLengthCharacters )
	{
		m_iTimer += delta
		if(m_iTimer > k_iTimeToPrint)
		{
			m_iTimer -= k_iTimeToPrint;
			updateLabel();
		}
	}
}

void updateLabel()
{
	String sNewLabelValue = m_pLabel.getString();

	// Find the character to update.
	final char cNew = m_iPrintedCharacters < m_sDisplayText.length() ? m_sDisplayText.charAt(m_iPrintedCharacters) : ' ';

	// Update the label value
	sNewLabelValue.replace(m_iPrintedCharacters, cNew);
	m_pLabel.setString(sNewLabelValue);

	++m_iPrintedCharacters;
}