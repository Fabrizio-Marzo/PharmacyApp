-- 01
CREATE FUNCTION `CurrentOperator`() RETURNS int
    DETERMINISTIC
BEGIN
			DECLARE CurrentOperator int ;
			SELECT ID_dipendente_operatore INTO CurrentOperator
			FROM ( dipendente_operatore D JOIN turno T ON (D.ID_dipendente_operatore = T.ID_dipendente))
			WHERE T.data_inizio= current_date()
			AND current_time() between convert(T.ora_inizio, time)
			AND convert(T.ora_fine, time);
            
RETURN CurrentOperator;
END;

-- 02
CREATE FUNCTION `OperatorWithMaxIncome`() RETURNS int
    DETERMINISTIC
BEGIN
-- dipendente con entrata maggiore --
  DECLARE Dipendente int; 
  DECLARE Guadagnoprec decimal(65,2);
  DECLARE Guadagno decimal (65,2);
  DECLARE IdDipendente int;
  DECLARE Mese int ;
  SET IdDipendente = (SELECT COUNT(*)
					  FROM dipendente_operatore);
  SET guadagno = 0;
  SET Guadagnoprec = 0;
  SET Dipendente = 0;
  SET Mese = month(now());
  
  WHILE IdDipendente > 1 DO
    SET Guadagnoprec = (SELECT SUM(totale_complessivo) 
						FROM scontrino
						WHERE ID_dipendente = IdDipendente
						AND MONTH(data_ora) = Mese);
        IF Guadagnoprec > guadagno THEN
			SET Guadagno = Guadagnoprec;
            SET Dipendente = IdDipendente;
        END IF;
    SET IdDipendente = IdDipendente -1;
  END WHILE;
RETURN Dipendente;
END;