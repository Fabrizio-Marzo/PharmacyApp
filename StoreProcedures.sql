-- 01 AggiuntaComposizione Componente 
CREATE PROCEDURE `AggiuntaComposizioneComponente`( in PrincipioAttivo char(200), QuantitàComposizione char(100), UnitàDiMisura char(100), FormulaChimica char(100))
BEGIN
  INSERT INTO componente(principio_attivo, quantità, unità_di_misura, formula_chimica)
        VALUES(PrincipioAttivo, QuantitàComposizione, UnitàDiMisura, FormulaChimica );
    
    INSERT INTO composizione( AIC_EAN, principio_attivo, quantità, unità_di_misura)
    VALUES ( (    
    SELECT AIC_EAN
    FROM oggetto_di_vendita
    WHERE servizio = 0
    ORDER BY ID_oggetto_di_vendita DESC
    LIMIT 1), PrincipioAttivo, QuantitàComposizione, UnitàDiMisura);     
END;

-- 02 
CREATE PROCEDURE `AggiuntaOfferta`()
BEGIN
  INSERT INTO offerta(nome, costo, AIC_EAN)
    VALUES ( 
    (SELECT P.nome 
    FROM oggetto_di_vendita as O, prodotto as P
    WHERE servizio = 0
    AND O.AIC_EAN = P.AIC_EAN
    ORDER BY ID_oggetto_di_vendita DESC
    LIMIT 1),
        ((SELECT prezzo
        FROM oggetto_di_vendita
    WHERE servizio = 0
    ORDER BY ID_oggetto_di_vendita DESC
    LIMIT 1
        )*0.95), 
        (SELECT AIC_EAN
        FROM oggetto_di_vendita
        WHERE servizio = 0
        ORDER BY ID_oggetto_di_vendita DESC
        LIMIT 1)
        );
END;

-- 03
CREATE PROCEDURE `AggiuntaProdotto`( in Descrizione char(200), Prezzo decimal(65,2), aic_ean char(15), ClasseParafarmaco char(200), CodiceEquivalenza char(100), TipoRicetta char(100), TipoProdotto char(100), Nome char(100), Produttore char(100), MetodoSomministrazione char(100), Quantità char(100))
BEGIN
  DECLARE IDOgg int; 
  SET IDOgg = ( SELECT COUNT(*)   FROM oggetto_di_vendita as O );
    
  INSERT INTO oggetto_di_vendita (servizio, ID_oggetto_di_vendita, AIC_EAN, descrizione, prezzo)
    VALUES (0, IDOgg+1 , aic_ean, Descrizione, Prezzo);
    
    INSERT INTO prodotto(classe_parafarmaco, codice_di_equivalenza, tipo_di_ricetta, AIC_EAN, tipo_prodotto, nome, produttore, quantità, metodo_di_somministrazione, giacenza_totale)
    VALUES (ClasseParafarmaco, CodiceEquivalenza, TipoRicetta, aic_ean, TipoProdotto, Nome, Produttore, Quantità, MetodoSomministrazione, 0);
END;

-- 04
CREATE PROCEDURE `AggiuntaScontrino`( in Pagamento char(100), CodiceFiscale char(16) )
BEGIN
	-- creazione scontrino --
    DECLARE CountNumScontrini int;
    SET CountNumScontrini = (SELECT COUNT(*)  
							 FROM scontrino);
  INSERT INTO scontrino ( numero_progressivo, data_ora, tipo_pagamento,CF_cliente,totale_complessivo, ID_dipendente)
  VALUES (CountNumScontrini+1, current_date() ,Pagamento , CodiceFiscale ,0.00, (CurrentOperator()));                      
END;

-- 05
CREATE PROCEDURE `AggiuntaServizio`( in Descrizione char(200), Prezzo decimal(65,2) )
BEGIN
  DECLARE IDOgg int; 
  SET IDOgg = ( SELECT COUNT(*)   FROM oggetto_di_vendita as O );
    
    INSERT INTO oggetto_di_vendita (servizio, ID_oggetto_di_vendita, AIC_EAN, descrizione, prezzo)
    VALUES (1, IDOgg+1 , null, Descrizione, Prezzo);
    
END;

-- 06 
CREATE PROCEDURE `CheckCatalogo`( in aic_ean char(15),  out result bool)
BEGIN

	DECLARE nameLastOrderStocklist char(100);
	DECLARE c int;
	SET nameLastOrderStocklist = (SELECT O.nome_fornitore
							FROM ordine as O
							ORDER BY numero_ordine DESC
							LIMIT 1);
	
	SELECT COUNT(*) into c
	FROM offerta as O
	WHERE O.AIC_EAN = aic_ean
    AND O.nome = nameLastOrderStocklist ;
    
  IF c = 0 THEN 
    SET result = FALSE;
  ELSE 
    SET result = TRUE;
  END IF;
END;

-- 07
CREATE PROCEDURE `CheckCatalogoUltimoOrdine`( in aic_ean char(15),  out result bool)
BEGIN

	DECLARE nameLastOrderStocklist char(100);
	DECLARE c int;
	SET nameLastOrderStocklist = (SELECT O.nome_fornitore
							FROM ordine as O
							ORDER BY numero_ordine DESC
							LIMIT 1);
	
	SELECT COUNT(*) into c
	FROM offerta as O
	WHERE O.AIC_EAN = aic_ean
    AND O.nome = nameLastOrderStocklist ;
    
  IF c = 0 THEN 
    SET result = FALSE; -- Non c'è il prodotto per il Fornitore Scelto -- 
  ELSE 
    SET result = TRUE;
  END IF;
END;

-- 08
CREATE PROCEDURE `CheckCurrentOperatorIsBestOperator`(out result boolean)
BEGIN
	DECLARE CurrentOperator int;
    DECLARE BestOperator int;
	SET CurrentOperator = CurrentOperator();
    SET BestOperator = OperatorWithMaxIncome();
    IF CurrentOperator = BestOperator THEN
		SET result = true;
	else
		SET result = false;
	END IF;
END;

-- 09
CREATE PROCEDURE `CheckGiacenza`( in ID_Oggetto bigint ,QuantitàOggettoInput int, OUT result BOOL)
BEGIN
  DECLARE Quantity int;
  DECLARE giacenza_totale int;
  
  SELECT P.giacenza_totale into Quantity
  FROM prodotto as P, oggetto_di_vendita as O
  WHERE P.AIC_EAN = O.AIC_EAN
  AND O.ID_oggetto_di_vendita = ID_Oggetto;
                  
  SET giacenza_totale = Quantity - QuantitàOggettoInput;
  
  IF giacenza_totale < 0 THEN 
	SET RESULT = FALSE; -- Significa che non posso farlo ---
  ELSE 
    SET RESULT = TRUE; -- Significa chee posso avanzare --
  END IF;
END;

-- 10
CREATE PROCEDURE `CheckLogin`(in Username char(100), Id_Dipendente int, out result bool)
BEGIN

	DECLARE c int;
	SELECT COUNT(*) into c
	FROM dipendente_operatore as DOP
	WHERE DOP.nome = Username
    AND DOP.ID_dipendente_operatore = Id_Dipendente;
IF c = 0 THEN 
    SET result = FALSE;
  ELSE 
    SET result = TRUE;
  END IF;

END;

-- 11
CREATE PROCEDURE `CheckProdottoPresente`( in NomeProdotto char(100) , out result boolean )
BEGIN
	DECLARE presenza int;
    SET presenza = (SELECT COUNT(*) FROM prodotto WHERE nome = NomeProdotto); 
    -- se il count restituisce un valore > 0, il prodotto è presente nella lista --
	IF presenza = 0 THEN 
		SET RESULT = FALSE; -- Non è presente nella Lista 0 -- 
	ELSE 
		SET RESULT = TRUE; -- E' presente nella lista 1 --
	END IF;
END;

-- 12
CREATE PROCEDURE `CheckQuantitàDanneggiata`(in NumeroLotto bigint, IDBolla bigint, QuantitàDaRendere int, out result bool)
BEGIN
  DECLARE quantity int; -- variabile per il check -- 
  DECLARE AIC_EAN_ByLotto char(15);
  SET AIC_EAN_ByLotto = (SELECT AIC_EAN
							FROM lotto
							WHERE numero_lotto = NumeroLotto);
  SET quantity = (SELECT quantità 
					FROM voce_bolla
                    WHERE AIC_EAN = AIC_EAN_ByLotto 
                    AND ID_bolla = IDBolla);
  IF QuantitàDaRendere > quantity THEN 
    SET result = false; -- Sto Inserendo una Quantità maggiore ( Non Va Bene) --
  ELSE 
    SET result = true; -- Sto Inserendo una quantità corretta -- 
  END IF;
END;

-- 13
CREATE PROCEDURE `CreazioneVoceOrdine`( in AIC_EAN char(15), quantità int)
BEGIN
	DECLARE NumOr int;
    DECLARE Posizione int;
    -- Prendo l'ultimo Ordine -- 
    SET NumOr = ( SELECT COUNT(*)   FROM ordine as O );
    SET Posizione = (SELECT COUNT(*) FROM voce_ordine WHERE numero_ordine = NumOr);
    
    INSERT INTO voce_ordine (AIC_EAN, numero_ordine, posizione_in_elenco, quantità, costo_unitario)
    VALUES(
    (SELECT AIC_EAN
    FROM (offerta OFE JOIN prodotto P ON (OFE.AIC_EAN = P.AIC_EAN))
    WHERE OFE.nome = P.produttore 
    AND OFE.AIC_EAN = AIC_EAN)
    , NumOr, Posizione+1, quantità, 
    (SELECT costo
    FROM offerta as O
    WHERE O.AIC_EAN= AIC_EAN)
    );
END;

-- 14
CREATE PROCEDURE `CreazioneVoceScontrino`( in  ID_Oggetto bigint, QuantitàOggetto int)
BEGIN
	DECLARE NumProg int;
    DECLARE Posizione int;
    
	SET NumProg = (SELECT COUNT(*) FROM scontrino);
    -- Posizione in elenco della voce scontrino --
	SET Posizione = (SELECT COUNT(*) FROM voce_scontrino WHERE numero_progressivo = NumProg);
    
  -- creazione voce scontrino --
        INSERT INTO voce_scontrino (ID_oggetto_di_vendita, numero_progressivo, posizione_in_elenco, quantità, prezzo_parziale)
        VALUES ( ID_Oggetto, NumProg , Posizione+1, QuantitàOggetto, 
        (SELECT O.prezzo*QuantitàOggetto
        FROM oggetto_di_vendita AS O
        WHERE ID_Oggetto= O.ID_oggetto_di_vendita));
        
        UPDATE scontrino SET totale_complessivo = totale_complessivo +
        (SELECT O.prezzo*QuantitàOggetto
        FROM oggetto_di_vendita AS O
        WHERE ID_Oggetto= O.ID_oggetto_di_vendita) WHERE  numero_progressivo= NumProg;
        
        UPDATE prodotto SET giacenza_totale = giacenza_totale - QuantitàOggetto WHERE AIC_EAN = 
        (SELECT AIC_EAN
		FROM oggetto_di_vendita 
		WHERE ID_oggetto_di_vendita = ID_Oggetto
    );
END;

-- 15
CREATE PROCEDURE `DipendenteEntrateMaggiori`( in mese_MM int, out Dipendente int, out Guadagno decimal(65,2)  )
BEGIN
-- dipendente con entrata maggiore --
 -- DECLARE Dipendente int; 
  DECLARE Guadagnoprec decimal(65,2);
 -- DECLARE Guadagno decimal (65,2);
  DECLARE IdDipendente int;
  SET IdDipendente = (SELECT COUNT(*)
					  FROM dipendente_operatore);
  SET guadagno = 0;
  SET Guadagnoprec = 0;
  SET Dipendente = 0;
  
  WHILE IdDipendente > 1 DO
    SET Guadagnoprec = (SELECT SUM(totale_complessivo) 
						FROM scontrino
						WHERE ID_dipendente = IdDipendente
						AND MONTH(data_ora) = mese_MM);
        IF Guadagnoprec > guadagno THEN
			SET Guadagno = Guadagnoprec;
            SET Dipendente = IdDipendente;
        END IF;
    SET IdDipendente = IdDipendente -1;
  END WHILE;
END;

-- 16
CREATE PROCEDURE `DipendenteEntrateMaggioriMeseAttuale`()
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
    SELECT Dipendente, Guadagno;

END;

-- 17
CREATE PROCEDURE `EffettuazioneOrdine`(in Fornitore char(100) )
BEGIN
  DECLARE NumOr int;
    SET NumOr = ( SELECT COUNT(*)   FROM ordine as O );
  INSERT INTO ordine (numero_ordine, data_ora, ordine_confermato, ID_dipendente, nome_fornitore)
    VALUES( NumOr+1, curdate(), '0', CurrentOperator(),Fornitore);
END;

-- 18
CREATE PROCEDURE `FarmaciEquivalenti`( in CodiceEquivalenza char(100) )
BEGIN
	SELECT *
    FROM prodotto
    WHERE codice_di_equivalenza = CodiceEquivalenza;
END;

-- 19
CREATE PROCEDURE `InserimentoBolla`( in NumeroOrdine int, NumVociBollaToAdd int)
BEGIN
	DECLARE CountNumLotti int;
    DECLARE CountVociOrderByOrder int; -- il numero di voci bolle non può essere maggiore delle voci ordini -- 
    DECLARE posizione int;
    DECLARE CountNumBolle int;
    DECLARE CheckConfermaByOrder int; 
    DECLARE CountVociBollaByOrder int;
    -- Conto il Numero di Lotti che ci sono nella Tabella lotto --
    SET CountNumLotti = (SELECT COUNT(*)  
				         FROM lotto);
	-- Conto il Numero di VociBolla su quel determinato Ordine, inizialmente a 0 ancora voci bolla--
    -- non creati, in questo caso --
    -- CountVociBollaByOrder serve per inserire il numero di bolla in modo progressivo --
    SET CountVociBollaByOrder = ( SELECT COUNT(*)
								  FROM ordine O, bolla BO, voce_bolla VB 
						          WHERE O.numero_ordine = NumeroOrdine
						          AND BO.numero_ordine = NumeroOrdine
						          AND BO.ID_bolla = VB.ID_bolla );
	-- Conto lo voci ordine da un determinato ordine -- 
    SET CountVociOrderByOrder = ( SELECT COUNT(*) 
								  FROM voce_ordine 
                                  WHERE numero_ordine = NumeroOrdine);
	SET posizione = 1;
    -- Conto il numero di bolle dentro la tabella Bolla --
    SET CountNumBolle = ( SELECT COUNT(*)  
						  FROM bolla as BO );
	-- Controllo se l'ordine nel quale voglio fare la bolla sia Confermato (1) oppure NO (0) -- 
    -- CheckConfermaByOrder serve per verificare che l'ordine sia stato Confermato --
    SET CheckConfermaByOrder = (SELECT ordine_confermato
								FROM ordine 
                                WHERE numero_ordine = NumeroOrdine);

-- Solo se l'ordine è stato confermato posso creare la bolla su quel ordine -- 
    IF CheckConfermaByOrder = 1 THEN 
	-- Inserisco la Nuova Bolla --
    -- L'ID_bolla va in modo progressivo-> 1,2,3,4....,Data odierna, NumOrdineInserito -- 
    INSERT INTO bolla (ID_bolla, data_ora_consegna, numero_ordine)
    VALUES( CountNumBolle+1, current_date(), NumeroOrdine);
		IF  (CountVociOrderByOrder - CountVociBollaByOrder) >= NumVociBollaToAdd  THEN
        WHILE NumVociBollaToAdd > 0 DO -- Inserisco ogni voce_Bolla -- 
        INSERT INTO voce_bolla (AIC_EAN, ID_bolla, posizione_in_elenco, quantità)
        VALUES(
                (SELECT AIC_EAN
                FROM voce_ordine
                WHERE numero_ordine = NumeroOrdine
                AND posizione_in_elenco = (CountVociBollaByOrder+posizione) )
                ,CountNumBolle+1, posizione, 
                (SELECT quantità
                FROM voce_ordine
                WHERE numero_ordine = NumeroOrdine
                AND posizione_in_elenco = (CountVociBollaByOrder+posizione))
                );
       
        -- Diminuisco il numero di voci da inserie 
        SET NumVociBollaToAdd = NumVociBollaToAdd-1;
                
		
		-- Inserimento lotto --
        SET CountNumLotti = CountNumLotti + 1;
		INSERT INTO lotto (AIC_EAN, numero_lotto, vendibile)
		VALUES ((SELECT AIC_EAN
                FROM voce_ordine
                WHERE numero_ordine = NumeroOrdine
                AND posizione_in_elenco = (CountVociBollaByOrder+posizione) ), CountNumLotti, 1 );
                
		-- inserimento scadenza --
		INSERT INTO scadenza (AIC_EAN, lotto, data_scadenza, quantità_in_scadenza)
		VALUES ((SELECT AIC_EAN
		         FROM voce_ordine
				 WHERE numero_ordine = NumeroOrdine
		         AND posizione_in_elenco = (CountVociBollaByOrder+posizione) ), CountNumLotti, 
                (date_add( current_date(), interval 1 year)), 
                (SELECT quantità
                FROM voce_ordine
                WHERE numero_ordine = NumeroOrdine
                AND posizione_in_elenco = (CountVociBollaByOrder+posizione)));
                
                -- modifica giacenza --
		UPDATE prodotto SET giacenza_totale= giacenza_totale + (SELECT quantità
                FROM voce_ordine
                WHERE numero_ordine = NumeroOrdine
                AND posizione_in_elenco = (CountVociBollaByOrder+posizione))
                WHERE AIC_EAN = (SELECT AIC_EAN
                FROM voce_ordine
                WHERE numero_ordine = NumeroOrdine
                AND posizione_in_elenco = (CountVociBollaByOrder+posizione));
                
	  SET posizione = posizione + 1;
      END WHILE;
    END IF;
  END IF;
END;

-- 20
CREATE PROCEDURE `InserimentoFattura`( in NumeroOrdine bigint, IDFattura int)
BEGIN
  DECLARE NumVO int; 
  SET NumVO = ( SELECT COUNT(*)   FROM voce_ordine as VO WHERE VO.numero_ordine= NumeroOrdine);
-- Inserire la fattura e calcolare il totale -- 
  INSERT INTO fattura (ID_fattura, numero_ordine, totale)
    VALUES ( IDFattura, NumeroOrdine, 0.00); -- Primo inserito -- 
        
    -- 2 -- 
    WHILE NumVO > 0 DO 
  
    UPDATE fattura SET totale = totale +
    (SELECT VO.costo_unitario*VO.quantità 
    FROM voce_ordine as VO
    WHERE VO.numero_ordine = NumeroOrdine
    AND NumVO = VO.posizione_in_elenco) WHERE  ID_fattura = IDFattura;
    
    SET NumVO = NumVO -1;
    
    END WHILE;
    
    -- Ordine Confermato -- 
   UPDATE ordine SET ordine_confermato = 1  
   WHERE numero_ordine = NumeroOrdine; 

END;

-- 21
CREATE PROCEDURE `MeseMassimoGuadagno`(in anno int, out mese int)
BEGIN
  DECLARE Count int;
    DECLARE guadagno decimal(65,2);
    DECLARE guadagno_mensile decimal(65,2);
    SET Count = 12;
    SET mese = 0;
    SET guadagno = 0.0;  -- variabile utile al confronto -- 
    WHILE Count > 0 DO 
    SET guadagno_mensile = 0.0;  
    SET guadagno_mensile = guadagno_mensile + (SELECT SUM(totale_complessivo) FROM scontrino
        WHERE MONTH(data_ora) = Count
        AND YEAR(data_ora) = anno);
        IF guadagno_mensile > guadagno THEN
      SET guadagno = guadagno_mensile;
            SET mese = Count;
        END IF;
    SET Count = Count -1;
    END WHILE;
    SELECT 
    CASE 
    WHEN mese=1 THEN 'gennaio' 
        WHEN mese=2 THEN 'febbraio'
        WHEN mese=3 THEN 'marzo'
        WHEN mese=4 THEN 'aprile'
        WHEN mese=5 THEN 'maggio'
        WHEN mese=6 THEN 'giugno'
        WHEN mese=7 THEN 'luglio'
        WHEN mese=8 THEN 'agosto'
        WHEN mese=9 THEN 'settembre'
        WHEN mese=10 THEN 'ottobre' 
        WHEN mese=11 THEN 'novembre'
        WHEN mese=12 THEN 'dicembre'
    END AS mese;
END;

-- 22
CREATE PROCEDURE `ModificaPagamentoCF`(in TipoPagamento char(100), CF char(16))
BEGIN
DECLARE NP BIGINT;
SET NP = (SELECT S.numero_progressivo FROM scontrino as S ORDER BY numero_progressivo DESC limit 1);

UPDATE scontrino SET tipo_pagamento = TipoPagamento WHERE numero_progressivo = NP;
UPDATE scontrino SET CF_cliente = CF WHERE numero_progressivo = NP;

END;

-- 23
CREATE PROCEDURE `OttegoUltimoTotale`(out risultato decimal(65,2))
BEGIN
	SELECT S.totale_complessivo into risultato
    FROM scontrino as S
	ORDER BY numero_progressivo DESC limit 1;
END;

-- 24
CREATE PROCEDURE `OttengoOggettoVendita`(in ID_Oggetto bigint)
BEGIN
	SELECT OV.*
    FROM oggetto_di_vendita AS OV
	WHERE ID_oggetto_di_vendita = ID_Oggetto;
END;

-- 25
CREATE PROCEDURE `OttengoUltimoVoceOrdine`()
BEGIN
SELECT VO.*
    FROM voce_ordine AS VO
	ORDER BY numero_ordine DESC, posizione_in_elenco DESC
    LIMIT 1;
END;

-- 26
CREATE PROCEDURE `OttengoUltimoVoceScontrino`()
BEGIN
	SELECT VS.*
    FROM voce_scontrino AS VS
	ORDER BY numero_progressivo DESC, posizione_in_elenco DESC
    LIMIT 1;
END;

-- 27
CREATE PROCEDURE `ResoNonVendibile`(in ID_reso int, NumeroLotto bigint, ID_Bolla bigint, QuantitàDaRendere int )
BEGIN
	DECLARE AIC_EAN_ByLotto char(15);
    SET AIC_EAN_ByLotto = ( SELECT AIC_EAN
						  FROM lotto
						  WHERE numero_lotto = NumeroLotto);
                          
-- inserisco il reso del prodotto --
    INSERT INTO reso( motivazione, ID_reso, ID_dipendente, nome_fornitore, numero_ordine)
    VALUES('Danneggiato', -- Motivazione --
			ID_reso, -- ID_reso -- 
            (CurrentOperator()), -- ID _ dipendente -- 
            (SELECT nome_fornitore -- Nome_Fornitore -- 
			FROM ((ordine O JOIN bolla B ON (O.numero_ordine = B.numero_ordine) ) JOIN voce_bolla VB ON (B.ID_bolla = VB.ID_bolla) JOIN lotto LO ON(VB.AIC_EAN = LO.AIC_EAN) )
			WHERE numero_lotto = NumeroLotto),
            (SELECT O.numero_ordine -- Numero_ordine -- 
			FROM ((ordine O JOIN bolla B ON (O.numero_ordine = B.numero_ordine) ) JOIN voce_bolla VB ON (B.ID_bolla = VB.ID_bolla) JOIN lotto LO ON(VB.AIC_EAN = LO.AIC_EAN) )
			WHERE numero_lotto = NumeroLotto));
           
	INSERT INTO riferimento_reso(ID, quantità_resa, AIC_EAN)
    VALUES (ID_reso,QuantitàDaRendere,AIC_EAN_ByLotto);
            
	UPDATE prodotto 
    SET giacenza_totale= giacenza_totale - QuantitàDaRendere
    WHERE AIC_EAN = AIC_EAN_ByLotto;
END;

-- 28
CREATE PROCEDURE `ResoSmaltimento`( in IDReso int, LottoScaduto bigint)
BEGIN    

	DECLARE QuantitàDaRendereScaduta bigint;
    DECLARE AIC_EAN_ByScadenza char(15);
    SET QuantitàDaRendereScaduta = (SELECT S.quantità_in_scadenza -- Prendo la quantità in scadenza da rendere --
									FROM scadenza as S, lotto as L
									WHERE current_date() > S.data_scadenza
									AND L.numero_lotto = LottoScaduto
									AND L.AIC_EAN = S.AIC_EAN); 
    SET AIC_EAN_ByScadenza = (SELECT S.AIC_EAN 
							  FROM scadenza as S
							  WHERE S.lotto = LottoScaduto);
    -- inserisco il 'reso' del prodotto --
    INSERT INTO reso( motivazione, ID_reso, ID_dipendente, nome_fornitore, numero_ordine)
    -- Scadenza -- 
    VALUES ('smaltimento', -- Motivazione -- 
			IDReso,  -- Id--
			CurrentOperator(), -- Id_Dipendente --  
			(SELECT P.produttore -- Nome_Fornitore --
			FROM (prodotto P JOIN scadenza S ON (P.AIC_EAN = S.AIC_EAN))
			WHERE current_date() > S.data_scadenza
			AND S.lotto = LottoScaduto),
            null); -- Numero Ordine a null, non serve per le scadenze  -- 
    
    -- Inserimento di una riga nella tabella 'riferimento_reso' -- 
	INSERT INTO riferimento_reso (ID, quantità_resa, AIC_EAN)
    VALUES (IDReso, -- Id_Reso --
			QuantitàDaRendereScaduta, -- Quantità da Rendere perchè Scaduto--
			(SELECT S.AIC_EAN
			FROM scadenza as S, lotto as L
			WHERE L.numero_lotto = LottoScaduto
			AND L.AIC_EAN = S.AIC_EAN));

    -- Metto il 'Lotto' a non vendibile (è scaduto) 
    UPDATE lotto SET vendibile = 0
    WHERE numero_lotto = LottoScaduto
    AND AIC_EAN = AIC_EAN_ByScadenza;
    
    -- Cambio la Giacenza in 'Prodotto' per la Scadenza--
    UPDATE prodotto SET giacenza_totale= giacenza_totale - QuantitàDaRendereScaduta
    WHERE AIC_EAN = AIC_EAN_ByScadenza;
END;

-- 29
CREATE PROCEDURE `ScadenzeImminenti`( in GiorniMancantiScadenza int)
BEGIN
  SELECT S.*
    FROM scadenza as S
    WHERE datediff(data_scadenza, current_date())<= GiorniMancantiScadenza
    AND data_scadenza >= current_date();
END

-- 30
CREATE PROCEDURE `StatisticaDashboardPerDipendente`()
BEGIN
	SELECT data_ora , SUM(totale_complessivo) 
	FROM scontrino
    WHERE ID_dipendente = CurrentOperator()
    GROUP BY data_ora
    ORDER BY TIMESTAMP(data_ora) DESC 
    LIMIT 9;
END;

-- 31
CREATE PROCEDURE `StatisticaDashboardPerDipendenteData`()
BEGIN
	
		SELECT scontrino.data_ora
		  FROM scontrino
          WHERE ID_dipendente = CurrentOperator()
          GROUP BY data_ora 
          ORDER BY TIMESTAMP(data_ora) DESC
		  LIMIT 9;
END;

-- 32
CREATE PROCEDURE `StatisticaDashboardPerDipendenteFatturato`()
BEGIN
	SELECT SUM(totale_complessivo)
	FROM scontrino
    WHERE ID_dipendente = CurrentOperator()
    GROUP BY data_ora
    ORDER BY TIMESTAMP(data_ora) DESC 
    LIMIT 9;
END;

-- 33
CREATE PROCEDURE `StatisticaDashboardperDipendenteMigliore`()
BEGIN
	SELECT data_ora , SUM(totale_complessivo) 
	FROM scontrino
    WHERE ID_dipendente = OperatorWithMaxIncome()
    GROUP BY data_ora
    ORDER BY TIMESTAMP(data_ora) DESC 
    LIMIT 9;
END;

-- 34
CREATE PROCEDURE `StatisticaDashboardperDipendenteMiglioreData`()
BEGIN
	SELECT scontrino.data_ora
	FROM scontrino
	WHERE ID_dipendente = OperatorWithMaxIncome()
	GROUP BY data_ora 
	ORDER BY TIMESTAMP(data_ora) DESC
	LIMIT 9;
END;

-- 35
CREATE PROCEDURE `StatisticaDashboardperDipendenteMiglioreFatturato`()
BEGIN
	SELECT SUM(totale_complessivo)
	FROM scontrino
    WHERE ID_dipendente = OperatorWithMaxIncome()
    GROUP BY data_ora
    ORDER BY TIMESTAMP(data_ora) DESC 
    LIMIT 9;
END;

-- 36
CREATE PROCEDURE `StatisticheGiornaliere`( in Data_AAAA_MM_GG date, out Guadagno decimal(65,2), out NumScontrini int)
BEGIN
  
    DECLARE CountScontrini int;
    DECLARE NumProgIniziale int;
    
    SET Guadagno = 0;
    SET CountScontrini = (SELECT COUNT(*) 
            FROM scontrino 
            WHERE data_ora = Data_AAAA_MM_GG);
  SET NumScontrini = CountScontrini;
  SET NumProgIniziale = (SELECT numero_progressivo
              FROM scontrino 
                            WHERE data_ora = Data_AAAA_MM_GG
                            LIMIT 1);
    -- assumiamo che gli scontrini di un giorno abbiano numero progressivo successivo --
    -- per costruzione dello scontrino --
    -- for per somma -- 
    WHILE CountScontrini>0 DO
    SET Guadagno = Guadagno + 
    (SELECT totale_complessivo
    FROM scontrino 
    WHERE data_ora = Data_AAAA_MM_GG
    AND numero_progressivo = NumProgIniziale);  
    SET CountScontrini = CountScontrini -1;
        SET NumProgIniziale = NumProgIniziale+1;
    END WHILE;
  
END;

-- 37
CREATE PROCEDURE `TotaleIntroito`(out totaleIntroito decimal(65,2))
BEGIN
SELECT SUM(totale_complessivo) into totaleIntroito
FROM scontrino
WHERE data_ora = current_date()
AND ID_dipendente = CurrentOperator();
END;

-- 38
CREATE PROCEDURE `TotaliClietiOggiPerDipendente`(out Totale int)
BEGIN
				SELECT COUNT(numero_progressivo) into Totale
				FROM scontrino
				WHERE data_ora = current_date()
				AND ID_dipendente = CurrentOperator();

END;

-- 39
CREATE PROCEDURE `TotaliProdottiDisponibili`(out TotaliProdottiDisponibili int)
BEGIN
SELECT COUNT(*) into TotaliProdottiDisponibili
FROM prodotto
WHERE giacenza_totale >0;
END;

-- 40
CREATE PROCEDURE `VedoFornitoriConOfferta`()
BEGIN
	SELECT DISTINCT O.nome
	FROM offerta AS O
    ORDER BY nome ASC;
END;

-- 41
CREATE PROCEDURE `VedoOffertaByFornitore`()
BEGIN
DECLARE nameLastOrderStocklist char(100);
SET nameLastOrderStocklist = (SELECT O.nome_fornitore
							FROM ordine as O
							ORDER BY numero_ordine DESC
							LIMIT 1);
SELECT O.*
FROM offerta as O
WHERE nome = nameLastOrderStocklist;
	
END;

-- 42
CREATE PROCEDURE `VisualizzazioneOrdiniBolleFatture`( in DataVisualizzazione char(100))
BEGIN   
-- tabelle unite -- 
    SELECT O.numero_ordine, O.data_ora, O.ordine_confermato, O.ID_dipendente, O.nome_fornitore, F.ID_fattura, F.totale, B.ID_bolla, B.data_ora_consegna
    FROM fattura as F, ordine as O, bolla as B
    WHERE O.data_ora = convert(DataVisualizzazione, date)
    AND F.numero_ordine = O.numero_ordine
    AND B.numero_ordine = O.numero_ordine;
    
END;

-- 43
CREATE PROCEDURE `VisualizzazioneProdotti`()
BEGIN
	SELECT P.*
    FROM prodotto P
	ORDER BY AIC_EAN DESC;
END;