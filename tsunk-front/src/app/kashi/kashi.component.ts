import { Component, OnInit } from '@angular/core';
import { HttpClient, HttpHandler } from '@angular/common/http';
import { HttpRequest } from '@angular/common/http';

import { Kashi, Kashis } from '../kashi'
import { Word, Words } from '../word'
import { InterceptorService } from '../service/interceptorService';

@Component({
  selector: 'app-kashi',
  templateUrl: './kashi.component.html',
  styleUrls: ['./kashi.component.css']
})

export class KashiComponent implements OnInit {
  getUrl = 'http://localhost:9000/api/kashi';
  getWordsUrl = 'http://localhost:9000/api/word';
  kashis: Kashi[];
  words: Word[];
  randWords: Word;

  constructor(
    private httpClient: HttpClient,
    private interceptorService: InterceptorService,
    private httpHandler: HttpHandler,
  ) { }

  ngOnInit() {
  }

  getKashis(): void {
    this.httpClient.get<Kashis>(this.getUrl).subscribe(result => {
      this.kashis = result.kashi
      console.log(this.kashis)
    })
  }

  getWords(): void {
    this.httpClient.get<Words>(this.getWordsUrl).subscribe(result => {
      this.words = result.word
    })
  }
}
